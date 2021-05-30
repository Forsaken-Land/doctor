package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.ChatEvent
import top.limbang.doctor.client.event.JoinGameEvent
import top.limbang.doctor.client.running.TpsUtils
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.DisconnectPacket
import top.limbang.doctor.protocol.entity.text.ChatGsonSerializer
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

    client.on(ChatEvent) {
        if (!it.chatPacket.json.contains("commands.forge.tps.summary")) {
            val chat = ChatGsonSerializer.jsonToChat(it.chatPacket.json)
            logger.info(chat.getUnformattedText())
        }
    }.onPacket<DisconnectPacket> {
        val reason = ChatGsonSerializer.jsonToChat(packet.reason)
        logger.info(reason.getUnformattedText())
    }.on(JoinGameEvent) {
        logger.info("登陆成功")

            val tps = TpsUtils(client)
            val result = tps.getTps()
            logger.info(result.toString())
            client.stop()

    }.on(ConnectionEvent.Disconnect) {
        logger.info("断开连接")
    }
}