package top.limbang.doctor.client.running

import com.google.gson.JsonParseException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.utils.asObservable
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
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
    private val tpsObservable = client.asObservable(PacketEvent(ChatPacket::class))
        .filter {
            //过滤Tps消息
            it.json.contains("commands.forge.tps.summary")
        }.map { (json) ->
            //吧tps消息的json解析
            parseTpsEntity(json)
        }.takeUntil {
            //一直解析到Overall
            it.dim == "Overall"
        }.timeout(5, TimeUnit.SECONDS) // 5秒超时
        .toList() //结果转换为列表

    fun getTps(): MutableList<TpsEntity> {
        client.sendMessage("/forge tps")
        //订阅一次流
        return tpsObservable.blockingGet()
    }

    private fun parseTpsEntity(json: String): TpsEntity {
        var chat = Json.parseToJsonElement(json).jsonObject
        while (!chat.containsKey("translate")) {
            if (chat.containsKey("extra")) {
                chat = chat.get("extra")!!.jsonObject
            } else {
                throw JsonParseException("tps格式不正确")
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

data class TpsEntity(val dim: String, val tickTime: Double, val tps: Double)
