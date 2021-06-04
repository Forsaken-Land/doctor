package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.event.ChatEvent
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.DisconnectPacket
import top.limbang.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import top.limbang.doctor.protocol.entity.text.ChatSerializer
import java.io.FileInputStream
import java.util.*


fun main() {
    val logger: Logger = LoggerFactory.getLogger("main")
    val pros = Properties()
    val file = FileInputStream("local.properties")
    pros.load(file)

    // 离线登陆测试
//    val host = "localhost"
//    val port = 25565
//    val name = pros["name"] as String

    // 外置正版登陆测试
    val host = pros["host"] as String
    val port = (pros["port"] as String).toInt()
    val username = pros["username"] as String
    val password = pros["password"] as String
    val authServerUrl = pros["authServerUrl"] as String
    val sessionServerUrl = pros["sessionServerUrl"] as String


    val client = MinecraftClient()
        //.name(name)
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .enablePlayerList()
        .enableForgeTps()
        .start(host, port)


    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }.on(ChatEvent) {
        if (!it.chatPacket.json.contains("commands.forge.tps.summary")) {
            val chat = ChatSerializer.jsonToChat(it.chatPacket.json)
            logger.info(chat.getFormattedText())
        }

    }.onPacket<DisconnectPacket> {
        val reason = ChatSerializer.jsonToChat(packet.reason)
        logger.warn(reason.getFormattedText())
    }.onPacket<PlayerPositionAndLookPacket> {
        logger.info("登录成功")
    }

    while (true) {
        when (val msg = readLine()) {
            "tps" -> {
                try {
                    val result = client.getForgeTps()
                    logger.info(result.toString())
                } catch (e: Exception) {
                }
            }


            "list" -> {
                val playerTab = client.getPlayerTab()
                logger.info(playerTab.toString())
                logger.info("玩家数量:${playerTab.players.size}")
                logger.info("更新时间:${playerTab.updateTime}")
                playerTab.players.map {
                    logger.info("name:${it.value.name} ping:${it.value.ping} gameMode:${it.value.gameMode}")
                }

            }
            else -> {
                if (!msg.isNullOrBlank()) {
                    client.sendMessage(msg)
                }
            }
//
//            else -> {
//                if (!msg.isNullOrBlank()) {
//                    if (msg.startsWith("/")) {
//                        client.connection
//                        if (client.getProtocol() > 348) { //TODO 找不到分辨方式
//                            val resp =
//                                client.connection.sendAndWait(TabCompleteEvent, CTabCompleteType1Packet(text = msg))
//                            if ((resp.sTabCompletePacket as STabCompleteType1Packet).matches.isNotEmpty()) {
//                                val words = msg.split(' ')
//                                val rest = if (words.size > 1) words.subList(0, words.size - 1) else emptyList()
//                                val result =
//                                    rest.toMutableList()
//                                        .also { it.add((resp.sTabCompletePacket as STabCompleteType1Packet).matches.first().match) }
//                                        .joinToString(" ")
//                                logger.info("自动补全命令：/${result}")
//                                client.sendMessage("/$result")
//                            } else {
//                                client.sendMessage(msg)
//                            }
//                        } else {
//                            val resp = client.connection.sendAndWait(TabCompleteEvent, CTabCompleteType0Packet(msg))
//                            if ((resp.sTabCompletePacket as STabCompleteType0Packet).matches.isNotEmpty()) {
//                                val words = msg.split(' ')
//                                val rest = if (words.size > 1) words.subList(0, words.size - 1) else emptyList()
//                                val result =
//                                    rest.toMutableList()
//                                        .also { it.add((resp.sTabCompletePacket as STabCompleteType0Packet).matches.first()) }
//                                        .joinToString(" ")
//                                logger.info("自动补全命令：${result}")
//                                client.sendMessage(result)
//                            } else {
//                                client.sendMessage(msg)
//                            }
//                        }
//
//                    } else {
//                        client.sendMessage(msg)
//                    }
//                }
//            }
        }

    }

}


