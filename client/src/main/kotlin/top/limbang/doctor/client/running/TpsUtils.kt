@file:Suppress("MemberVisibilityCanBePrivate")

package top.limbang.doctor.client.running

import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.entity.ForgeFeature.FML1
import top.limbang.doctor.client.entity.ForgeFeature.FML2
import top.limbang.doctor.client.utils.asObservable
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.protocol.definition.play.client.ChatType0Packet
import top.limbang.doctor.protocol.definition.play.client.ChatType1Packet
import java.util.concurrent.TimeUnit

/**
 * 解析tps工具（基于 forge tps 命令)
 * @author WarmthDawn
 * @since 2021-05-18
 */
class TpsUtils(
    val client: MinecraftClient
) {
    //观察流（基于Chat事件）
    private val tpsObservable = when (client.getForgeFeature()) {
        FML1 -> client.asObservable(PacketEvent(ChatType0Packet::class))
            .filter {
                //过滤Tps消息
                it.json.contains("commands.forge.tps.summary")
            }.map {
                //吧tps消息的json解析
                fml1ParseTpsEntity(it.json)
            }.takeUntil {
                //一直解析到Overall
                it.dim == "Overall"
            }
            .toList() //结果转换为列表
        FML2 -> client.asObservable(PacketEvent(ChatType1Packet::class))
            .filter {
                //过滤Tps消息
                (it.json.contains("commands.forge.tps.summary.all")
                        || it.json.contains("commands.forge.tps.summary.named"))
            }.map {
                //吧tps消息的json解析
                fml2ParseTpsEntity(it.json)
            }.takeUntil {
                //一直解析到Overall
                it.dim == "Overall"
            }
            .toList() //结果转换为列表
        null -> throw RuntimeException("原版无forge")
    }


    fun getTps(timeout: Long, unit: TimeUnit): MutableList<TpsEntity> {
        //订阅一次流
        return Single.create<MutableList<TpsEntity>> { s ->
            tpsObservable.subscribe { it: MutableList<TpsEntity> ->
                s.onSuccess(it)
            }
            client.sendMessage("/forge tps")
        }.timeout(timeout, unit).blockingGet()

    }

    fun getTps(): MutableList<TpsEntity> {
        return getTps(5, TimeUnit.SECONDS)
    }

    companion object {
        private fun getChat(json: String): JsonObject {
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

        fun fml2ParseTpsEntity(json: String): TpsEntity {
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

        fun fml1ParseTpsEntity(json: String): TpsEntity {
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
}

data class TpsEntity(val dim: String, val tickTime: Double, val tps: Double)
