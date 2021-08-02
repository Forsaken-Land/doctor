package top.fanua.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.fanua.doctor.allLoginPlugin.enableAllLoginPlugin
import top.fanua.doctor.client.running.*
import top.fanua.doctor.client.running.tabcomplete.TabCompletePlugin
import top.fanua.doctor.client.running.tabcomplete.tabCompleteTool
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.laggoggles.getLag
import top.fanua.doctor.protocol.definition.play.client.ChatPacket
import top.fanua.doctor.protocol.definition.play.client.DisconnectPacket
import top.fanua.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import top.fanua.doctor.protocol.definition.play.client.STabCompletePacket
import top.fanua.doctor.protocol.entity.text.ChatSerializer


fun main() {
    // 离线登陆测试
//    val host = "localhost"
//    val port = 25565
//    val name = pros["name"] as String

    val client = MinecraftClient.builder()
        //.name(name)
        .user(username, password)
//        .authServerUrl(authServerUrl)
//        .sessionServerUrl(sessionServerUrl)
//        .plugin(PluginLagGoggles())
        .plugin(PlayerPlugin())
        .plugin(AutoVersionForgePlugin())
        .plugin(TabCompletePlugin())
        .plugin(TpsPlugin())
        .enableAllLoginPlugin()
        .build()

    if (!client.start(host, port)) return

    client.on(ConnectionEvent.Disconnect) {
        return@on
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
    }.onPacket<STabCompletePacket> {
        logger.info("tab")
    }

    val job = GlobalScope.launch {
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
                    logger.info("玩家数量:${playerTab.players.size}")
                    logger.info("更新时间:${playerTab.updateTime}")
                    playerTab.players.map {
                        logger.info("name:${it.value.name} ping:${it.value.ping} gameMode:${it.value.gameMode}")
                    }

                }
                else -> {
                    if (msg?.startsWith("/") == true) {
                        try {
                            val tab = client.tabCompleteTool.getCompletions(msg)
//                        logger.info(tab.joinToString())
                        } catch (e: Exception) {
                        }
                    }
                    if (!msg.isNullOrBlank()) {
                        client.sendMessage(msg)
                    }
                }
//
//
            }

        }
    }
//    Thread.sleep(3000)
//    client.sendPacket(EntityActionPacket(entity, 0, 0))
//    val time = 50L
//    val size = 2
//    while (true) {
//        client.sendPacket(CPlayerPositionAndLookPacket(x + size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x + size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z + size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z + size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x - size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x - size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z - size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z - size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//    }
}


