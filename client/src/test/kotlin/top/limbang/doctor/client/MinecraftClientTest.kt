package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.utils.ProfileUtils
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.entity.text.ChatGsonSerializer


private val logger: Logger = LoggerFactory.getLogger("MAIN")
fun main() {
//    val host = "localhost"
//    val port = 25565
    val host = "mc.blackyin.xyz"
    val port = 524

//    val pingJson = MinecraftClient.ping(host, port).get()
//    println(AutoUtils.autoVersion(pingJson))
//    println(AutoUtils.autoForgeVersion(pingJson))


    val client = MinecraftClient()
        .user("tfgv852@qq.com", "12345678")
        .authServerUrl("https://skin.blackyin.xyz/api/yggdrasil/authserver")
        .sessionServerUrl("https://skin.blackyin.xyz/api/yggdrasil/sessionserver")
        .start(host, port)

    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }.onPacket<ChatPacket> {
        val chat = ChatGsonSerializer.jsonToChat(packet.json)
        ProfileUtils.exectueTime("聊天格式化") {
            logger.info(chat.getFormattedText())
        }
    }

    while (true){
        val msg = readLine()
        client.sendMessage(msg!!)

    }
}

