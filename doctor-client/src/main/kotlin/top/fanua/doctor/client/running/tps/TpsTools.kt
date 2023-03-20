@file:Suppress("MemberVisibilityCanBePrivate")

package top.fanua.doctor.client.running.tps

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.entity.ForgeFeature.FML1
import top.fanua.doctor.client.entity.ForgeFeature.FML2
import top.fanua.doctor.client.running.forgeFeature
import top.fanua.doctor.client.utils.asObservable
import top.fanua.doctor.network.handler.PacketEvent
import top.fanua.doctor.protocol.definition.play.client.ChatPacket
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


interface ITpsTools {
    val defaultTimeout: Pair<Long, TimeUnit>
    fun getTps(
        timeout: Long = defaultTimeout.first, unit: TimeUnit = defaultTimeout.second,
        callback: (error: Throwable?, List<TpsEntity>) -> Unit
    ): Disposable

    suspend fun getTpsSuspend(
        timeout: Long = defaultTimeout.first,
        unit: TimeUnit = defaultTimeout.second
    ): List<TpsEntity> =
        suspendCancellableCoroutine { cont ->
            val disposable = getTps(timeout, unit) { err, it ->
                if (err != null) {
                    cont.resumeWithException(err)
                } else {
                    cont.resume(it)
                }
            }
            cont.invokeOnCancellation {
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }

    fun getTps(timeout: Long = defaultTimeout.first, unit: TimeUnit = defaultTimeout.second): Future<List<TpsEntity>> {
        return Single.create { obs ->
            val disp = getTps(timeout, unit) { err, it ->
                if (err != null) {
                    obs.onError(err)
                } else {
                    obs.onSuccess(it)
                }
            }
            obs.setCancellable {
                disp.dispose()
            }
        }.toFuture()
    }

}

/**
 * 空白的TpsTools
 */
object DummyTpsTools : ITpsTools {
    override val defaultTimeout: Pair<Long, TimeUnit> = Pair(0, TimeUnit.SECONDS)

    override fun getTps(
        timeout: Long,
        unit: TimeUnit,
        callback: (error: Throwable?, List<TpsEntity>) -> Unit
    ): Disposable {
        throw TpsException("当前服务器不支持查询Tps")
    }

}

class TpsException(msg: String) : RuntimeException(msg)

abstract class TpsTools(
    val client: MinecraftClient,
    timeout: Long = 5,
    timeUnit: TimeUnit = TimeUnit.SECONDS
) : ITpsTools {
    abstract fun parseEntity(json: String): TpsEntity
    abstract val tpsObservable: Single<MutableList<TpsEntity>>
    override fun getTps(
        timeout: Long, unit: TimeUnit,
        callback: (error: Throwable?, List<TpsEntity>) -> Unit
    ): Disposable {
        val result = tpsObservable.timeout(timeout, unit)
            .subscribe { it, err ->
                if (err != null) {
                    callback(err, emptyList())
                } else {
                    callback(null, it)
                }
            }
        client.sendMessage("/forge tps")
        return result
    }

    override val defaultTimeout: Pair<Long, TimeUnit> = Pair(timeout, timeUnit)

    companion object {
        fun getChat(json: String): JsonObject {
            var chat = Json.parseToJsonElement(json).jsonObject
            while (!chat.containsKey("translate")) {
                if (chat.containsKey("extra")) {
                    chat = chat["extra"]!!.jsonObject
                } else {
                    throw SerializationException("tps格式不正确")
                }
            }
            return chat
        }

        fun create(client: MinecraftClient): ITpsTools {
            return when (client.forgeFeature) {
                FML1 -> TpsToolsFML1(client)
                FML2 -> TpsToolsFML2(client)
                null -> DummyTpsTools
            }
        }
    }
}

class TpsToolsFML1(client: MinecraftClient) : TpsTools(client) {
    override val tpsObservable = client.asObservable(PacketEvent(ChatPacket::class)).observeOn(Schedulers.io())
        .filter {
            //过滤Tps消息
            it.json.contains("commands.forge.tps.summary")
        }.map {
            //吧tps消息的json解析
            parseEntity(it.json)
        }.takeUntil {
            //一直解析到Overall
            it.dim == "Overall"
        }
        .toList() //结果转换为列表

    override fun parseEntity(json: String): TpsEntity {
        val chat = getChat(json)
        val (dim, tickTime, tps) = chat["with"]!!.jsonArray.map {
            it.jsonPrimitive.content
        }

        return TpsEntity(
            dim,
            tickTime.toDouble(),
            tps.toDouble()
        )
    }

}

class TpsToolsFML2(client: MinecraftClient) : TpsTools(client) {
    override val tpsObservable = client.asObservable(PacketEvent(ChatPacket::class)).observeOn(Schedulers.io())
        .filter {
            //过滤Tps消息
            it.json.contains("commands.forge.tps.summary.all") ||
                    it.json.contains("commands.forge.tps.summary.named")
        }.takeUntil {
            //一直解析到Overall
            it.json.contains("commands.forge.tps.summary.all")
        }.map {
            //吧tps消息的json解析
            parseEntity(it.json)
        }
        .toList() //结果转换为列表

    override fun parseEntity(json: String): TpsEntity {
        val chat = getChat(json)
        return when (chat["translate"]!!.jsonPrimitive.content) {
            "commands.forge.tps.summary.named" -> {
                val (dim, _, tickTime, tps) = chat["with"]!!.jsonArray.map {
                    it.jsonPrimitive.content
                }
                TpsEntity(
                    dim,
                    tickTime.toDouble(),
                    tps.toDouble()
                )
            }
            "commands.forge.tps.summary.all" -> {
                val (tickTime, tps) = chat["with"]!!.jsonArray.map {
                    it.jsonPrimitive.content
                }
                TpsEntity(
                    "Overall",
                    tickTime.toDouble(),
                    tps.toDouble()
                )
            }
            else -> throw RuntimeException("未收录此forge")
        }
    }

}

data class TpsEntity(val dim: String, val tickTime: Double, val tps: Double)


