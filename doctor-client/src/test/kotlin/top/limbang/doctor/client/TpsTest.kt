package top.limbang.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.limbang.doctor.client.utils.substringBetween
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.network.handler.oncePacket
import top.limbang.doctor.protocol.definition.play.client.JoinGamePacket
import top.limbang.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket

fun main() {

    val client = MinecraftClient()
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .enablePlayerList()

    if (!client.start(host, port)) return

    client.oncePacket<JoinGamePacket> {
        logger.info("登陆成功，开始发送 forge tps 指令")
    }.onPacket<PlayerPositionAndLookPacket> {
        GlobalScope.launch {
            val forgeTps = client.tpsTools.getTpsSuspend()
            var outMsg = "[123]低于20TPS的维度如下:\n"
            forgeTps.forEach { tpsEntity ->
                val dim = tpsEntity.dim.substringBetween("Dim", "(").trim()
                outMsg += when {
                    tpsEntity.dim == "Overall" -> "\n全局TPS:${tpsEntity.tps} Tick时间:${tpsEntity.tickTime}"
                    tpsEntity.tps < 20 -> "TPS:%-4.4s 维度:%s\n".format(tpsEntity.tps, dim)
                    else -> ""
                }
            }
            logger.info(outMsg)
            client.stop()
        }
    }
}