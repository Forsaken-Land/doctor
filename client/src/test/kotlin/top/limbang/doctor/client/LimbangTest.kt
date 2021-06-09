package top.limbang.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.JoinGameEvent
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

    // 外置正版登陆测试
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
        .enablePlayerList()
        .enableLag()

    if(!client.start(host, port)) return

    client.once(JoinGameEvent) {
        logger.info( "登陆成功，开始发送 forge tps 指令")
    }.onPacket<PlayerPositionAndLookPacket> {
        GlobalScope.launch {
            val forgeTps = client.getForgeTps()
            var outMsg = "[123]低于20TPS的维度如下:\n"
            forgeTps.forEach { tpsEntity ->
                val dim = tpsEntity.dim.substringBetween("Dim", "(").trim()
                outMsg += when {
                    tpsEntity.dim == "Overall" -> "\n全局TPS:${tpsEntity.tps} Tick时间:${tpsEntity.tickTime}"
                    tpsEntity.tps < 20 -> "TPS:%-4.4s 维度:%s\n".format(tpsEntity.tps, dim)
                    else -> ""
                }
            }
            logger.info( outMsg)
            client.stop()
        }
    }
}