package top.limbang.doctor.client

import top.limbang.doctor.client.running.*
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.plugin.laggoggles.PluginLagGoggles
import top.limbang.doctor.plugin.laggoggles.getLag
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.definition.play.client.DisconnectPacket
import top.limbang.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import top.limbang.doctor.protocol.entity.text.ChatSerializer


fun main() {
    // 离线登陆测试
//    val host = "localhost"
//    val port = 25565
//    val name = pros["name"] as String

    val client = MinecraftClient.builder()
        //.name(name)
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .plugin(PluginLagGoggles())
        .plugin(PlayerPlugin())
        .plugin(AutoVersionForgePlugin())
        .plugin(TpsPlugin())
        .build()

    if (!client.start(host, port)) return

    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }.onPacket<ChatPacket> {
        if (!packet.json.contains("commands.forge.tps.summary")) {
            val chat = ChatSerializer.jsonToChat(packet.json)
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
            "test" -> {
                try {
                    val result = client.getLag().get()
                    logger.info(result.toString())
                } catch (e: Exception) {
                    logger.error(e.message)
                }
            }
            "tps" -> {
                try {
                    val result = client.tpsTools.getTps().get()
                    logger.info(result.toString())
                } catch (e: Exception) {
                    logger.error("获取tps失败", e)
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


