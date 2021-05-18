package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.utils.ProfileUtils
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.entity.text.ChatGsonSerializer
import java.io.FileInputStream
import java.util.*


private val logger: Logger = LoggerFactory.getLogger("MAIN")
fun main() {
//    val host = "localhost"
//    val port = 25565
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

    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }.onPacket<ChatPacket> {
        val chat = ChatGsonSerializer.jsonToChat(packet.json)
        logger.info(chat.getFormattedText())
//        ProfileUtils.exectueTime("聊天格式化") {
//            logger.info(chat.getFormattedText())
//        }
    }

    while (true){
        val msg = readLine()
        client.sendMessage(msg!!)

    }
}

