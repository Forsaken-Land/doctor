package top.limbang.doctor.client

import top.limbang.doctor.client.running.AutoVersionForgePlugin
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.network.handler.oncePacket
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import top.limbang.doctor.protocol.entity.text.ChatSerializer

fun main() {

    val client = MinecraftClient.builder()
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .plugin(AutoVersionForgePlugin())
        .build()

    if (!client.start(host, port)) return

    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }.onPacket<ChatPacket> {
        val chat = ChatSerializer.jsonToChat(packet.json)
        logger.info(chat.getUnformattedText())
    }.oncePacket<PlayerPositionAndLookPacket> {
        logger.info("当前坐标(X:${packet.x.toInt()} Y:${packet.y.toInt()} Z:${packet.z.toInt()})")
    }

    while (true) {
        when (val msg = readLine()) {
            "stop" -> {
                client.stop()
                break
            }
            else -> {
                if (!msg.isNullOrBlank()) {
                    client.sendMessage(msg)
                }
            }
        }
    }

}
