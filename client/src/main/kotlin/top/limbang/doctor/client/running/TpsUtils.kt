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
 *
 * @author WarmthDawn
 * @since 2021-05-18
 */
class TpsUtils(
    val client: MinecraftClient
) {
    private val tpsObservable = client.asObservable(PacketEvent(ChatPacket::class))
        .filter {
            it.json.contains("commands.forge.tps.summary")
        }.map { (json) ->
            parseTpsEntity(json)
        }.takeUntil {
            it.dim == "Overall"
        }.timeout(5, TimeUnit.SECONDS)
        .toList()

    fun getTps(): MutableList<TpsEntity> {
        client.sendMessage("/forge tps")
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
