package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.ChatEvent
import top.limbang.doctor.client.event.JoinGameEvent
import top.limbang.doctor.client.running.TpsEntity
import top.limbang.doctor.client.running.TpsUtils
import top.limbang.doctor.client.utils.substringBetween
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import java.io.FileInputStream
import java.util.*

fun main() {

    val logger: Logger = LoggerFactory.getLogger("main")
    val pros = Properties()
    val file = FileInputStream("local.properties")
    pros.load(file)
    val host = pros["host"] as String
    val port = (pros["port"] as String).toInt()
    val username = pros["username"] as String
    val password = pros["password"] as String
    val authServerUrl = pros["authServerUrl"] as String
    val sessionServerUrl = pros["sessionServerUrl"] as String

    val client = MinecraftClient()
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .start(host, port)

    val tpsList = mutableListOf<TpsEntity>()
    client.on(ChatEvent) {
        if (it.chatPacket.json.contains("commands.forge.tps.summary")) {
            val tpsEntity = TpsUtils.parseTpsEntity(it.chatPacket.json)
            tpsList.add(tpsEntity)
            if (tpsEntity.dim != "Overall") return@on

            var outMsg = "[XX服务器]低于20TPS如下:\n"
            tpsList.filterIndexed { index, tpsEntity ->
                val dim = tpsEntity.dim.substringBetween("Dim", "(").trim()
                outMsg += when {
                    index == tpsList.size - 1 -> {
                        "\n全局TPS:${tpsEntity.tps} Tick时间:${tpsEntity.tickTime}\n"
                    }
                    tpsEntity.tps < 20 -> "TPS:%-4.4s 维度:%s\n".format(tpsEntity.tps, dim)
                    else -> ""
                }
                true
            }
            println(outMsg)
            client.stop()
        }
    }.once(JoinGameEvent) {
        logger.info("登陆成功")
    }.onPacket<PlayerPositionAndLookPacket> {
        client.sendMessage("/forge tps")
    }


}