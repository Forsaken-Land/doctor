@file:Suppress("MemberVisibilityCanBePrivate")

package top.limbang.doctor.client.running

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.event.ChatEvent
import top.limbang.doctor.client.utils.asObservable
import java.util.concurrent.TimeUnit

/**
 * 解析tps工具（基于 forge tps 命令)
 * @author WarmthDawn
 * @since 2021-05-18
 */
class TpsUtils(
    val client: MinecraftClient
) {
    //观察流（基于ChatPacket事件）
    private val tpsObservable = client.asObservable(ChatEvent)
        .filter {
            //过滤Tps消息
            it.chatPacket.json.contains("commands.forge.tps.summary")
        }.map { (_, chatPacket) ->
            //吧tps消息的json解析
            parseTpsEntity(chatPacket.json)
        }.takeUntil {
            //一直解析到Overall
            it.dim == "Overall"
        }
        .toList() //结果转换为列表

    fun getTps(timeout: Long, unit: TimeUnit): MutableList<TpsEntity> {
        client.sendMessage("/forge tps")
        //订阅一次流
        return tpsObservable.timeout(timeout, unit).blockingGet()
    }

    fun getTps(): MutableList<TpsEntity> {
        return getTps(5,TimeUnit.SECONDS)
    }

    companion object {
        fun parseTpsEntity(json: String): TpsEntity {
            var chat = Json.parseToJsonElement(json).jsonObject
            while (!chat.containsKey("translate")) {
                if (chat.containsKey("extra")) {
                    chat = chat.get("extra")!!.jsonObject
                } else {
                    throw SerializationException("tps格式不正确")
                }
            }
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
