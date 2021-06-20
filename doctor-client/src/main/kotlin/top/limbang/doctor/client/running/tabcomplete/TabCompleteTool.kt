package top.limbang.doctor.client.running.tabcomplete

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.suspendCancellableCoroutine
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.utils.asSingle
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.protocol.definition.play.client.STabCompletePacket
import top.limbang.doctor.protocol.definition.play.client.STabCompleteType0Packet
import top.limbang.doctor.protocol.definition.play.client.STabCompleteType1Packet
import top.limbang.doctor.protocol.definition.play.server.CTabCompletePacket
import top.limbang.doctor.protocol.definition.play.server.CTabCompleteType0Packet
import top.limbang.doctor.protocol.definition.play.server.CTabCompleteType1Packet
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *
 * @author WarmthDawn
 * @since 2021-06-20
 */
interface ITabCompleteTool {
    val defaultTimeout: Pair<Long, TimeUnit>
    fun getCompletions(
        prefix: String,
        timeout: Long = defaultTimeout.first, unit: TimeUnit = defaultTimeout.second,
        callback: (error: Throwable?, List<String>) -> Unit
    ): Disposable

    suspend fun getCompletionsSuspend(
        prefix: String,
        timeout: Long = defaultTimeout.first,
        unit: TimeUnit = defaultTimeout.second
    ): List<String> =
        suspendCancellableCoroutine { cont ->
            val disposable = getCompletions(prefix, timeout, unit) { err, it ->
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

    fun getCompletions(
        prefix: String,
        timeout: Long = defaultTimeout.first,
        unit: TimeUnit = defaultTimeout.second
    ): Future<List<String>> {
        return Single.create<List<String>?> { obs ->
            val disp = getCompletions(prefix, timeout, unit) { err, it ->
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

abstract class AbstractTabCompleteTool(
    val client: MinecraftClient,
    timeout: Long = 3,
    timeUnit: TimeUnit = TimeUnit.SECONDS
) : ITabCompleteTool {
    override fun getCompletions(
        prefix: String,
        timeout: Long,
        unit: TimeUnit,
        callback: (error: Throwable?, List<String>) -> Unit
    ): Disposable {
        val result = tabCompleteObs.timeout(timeout, unit)
            .subscribe { it, err ->
                if (err != null) {
                    callback(err, emptyList())
                } else {
                    callback(null, it)
                }
            }
        client.sendPacket(createPacket(prefix))
        return result
    }

    abstract fun createPacket(prefix: String): CTabCompletePacket

    abstract val tabCompleteObs: Single<List<String>>

    override val defaultTimeout: Pair<Long, TimeUnit> = Pair(timeout, timeUnit)

}

class TabCompleteTool112(client: MinecraftClient) : AbstractTabCompleteTool(client) {
    override fun createPacket(prefix: String): CTabCompletePacket {
        return CTabCompleteType0Packet(prefix)
    }

    override val tabCompleteObs: Single<List<String>> =
        client.asSingle(PacketEvent(STabCompletePacket::class))
            .observeOn(Schedulers.io())
            .map { it as STabCompleteType0Packet }
            .map { it.matches.toList() }
}

class TabCompleteTool116(client: MinecraftClient) : AbstractTabCompleteTool(client) {
    override fun createPacket(prefix: String): CTabCompletePacket {
        return CTabCompleteType1Packet(0, prefix)
    }

    override val tabCompleteObs: Single<List<String>> =
        client.asSingle(PacketEvent(STabCompletePacket::class))
            .observeOn(Schedulers.io())
            .map { it as STabCompleteType1Packet }
            .map { it.matches.map { m-> m.match } }
}

