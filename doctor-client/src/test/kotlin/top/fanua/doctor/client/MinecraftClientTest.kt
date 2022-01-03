package top.fanua.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.fanua.doctor.allLoginPlugin.enableAllLoginPlugin
import top.fanua.doctor.client.running.*
import top.fanua.doctor.client.running.tabcomplete.TabCompletePlugin
import top.fanua.doctor.client.running.tabcomplete.tabCompleteTool
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.plugin.forge.definations.fml1.Ids
import top.fanua.doctor.plugin.forge.definations.fml1.RegistryDataPacket
import top.fanua.doctor.plugin.ftbquests.PluginFtbQuests
import top.fanua.doctor.plugin.ftbquests.definations.MessageClaimAllRewardsPacket
import top.fanua.doctor.protocol.definition.play.client.*
import top.fanua.doctor.protocol.definition.play.server.*
import top.fanua.doctor.protocol.entity.BlockState
import top.fanua.doctor.protocol.entity.World
import top.fanua.doctor.protocol.entity.text.ChatSerializer
import java.math.RoundingMode


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
//        .plugin(PluginLagGoggles())
        .plugin(PlayerStatusPlugin())
        .plugin(PluginFtbQuests())
        .plugin(PlayerPlugin())
        .plugin(AutoVersionForgePlugin())
        .plugin(TabCompletePlugin())
        .plugin(TpsPlugin())
        .enableAllLoginPlugin()
        .build()

    if (!client.start(host, port)) return

    var x = 0
    var y = 0
    var z = 0
    val world = World()
    val bag = mutableMapOf<Int, SlotData>()
    val blocks = mutableListOf<Ids>()
    val items = mutableListOf<Ids>()
    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(1000L)
        client.reconnect()
    }.onPacket<RegistryDataPacket> {
        if (packet.name == "minecraft:blocks") {
            blocks.addAll(packet.ids)
        }
        if (packet.name == "minecraft:items") {
            items.addAll(packet.ids)
        }
//        println(packet.name)
//        if (packet.name.contains("bloodmagic")) println(packet.ids)
    }
        .onPacket<ChatPacket> {
            if (!packet.json.contains("commands.forge.tps.summary")) {
                val chat = ChatSerializer.jsonToChat(packet.json)
                logger.info(chat.getFormattedText())
            }

        }.onPacket<DisconnectPacket> {
            val reason = ChatSerializer.jsonToChat(packet.reason)
            logger.warn(reason.getFormattedText())
        }.onPacket<PlayerPositionAndLookPacket> {
            val tempX = packet.x.toBigDecimal().setScale(0, RoundingMode.DOWN).toInt()
            y = packet.y.toInt()
            val tempZ = packet.z.toBigDecimal().setScale(0, RoundingMode.DOWN).toInt()
            x = if (tempX >= 0) tempX else tempX - 1
            z = if (tempZ >= 0) tempZ else tempZ - 1
        }.onPacket<WindowItemsPacket> {
            if (packet.windowsId == 0) packet.slotData.forEach { (t, u) -> bag[t] = u }
        }.onPacket<SetSlotPacket> {
            if (packet.windowId == 0) bag[packet.slot] = packet.slotData
        }
        .onPacket<CustomPayloadPacket> {
            client.sendPacket(MessageClaimAllRewardsPacket())
            println(packet)
        }
        .onPacket<BlockChangePacket> {
            world.set(packet.blockPosition, BlockState(packet.blockId shr 4, packet.blockId and 15))
        }
        .onPacket<ChunkDataType0Packet> {
            if (packet.availableSections > 0) {
                world.chunks[Pair(packet.chunkX, packet.chunkZ)] = packet.chunk
            }
        }.replyPacket<SConfirmTransactionPacket> {
            CConfirmTransactionPacket(it.windowId, it.actionNumber, it.accepted)
        }
    var yaw = 0f
    var pitch = 0f
    val job = GlobalScope.launch {
        while (true) {
            when (val msg = readLine()) {
                "1" -> {
                    client.sendPacket(MessageClaimAllRewardsPacket())
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
                        } catch (_: Exception) {
                        }
                    }
                    if (!msg.isNullOrBlank()) {
                        client.sendMessage(msg)
                    }
                }
            }

        }
    }
    GlobalScope.launch {
        while (true) {
            try {
                client.sendPacket(EntityActionPacket(0, 1, 0))
                delay(1000)
                client.sendPacket(EntityActionPacket(0, 0, 0))
                delay(1000)
            } catch (_: Exception) {
            }
        }
    }
    GlobalScope.launch {
        while (true) {
            try {
                delay(1000)
                while (bag.isNotEmpty() && !client.connection.isClosed()) {
                    for (i in 0 until 50) {
                        val data = bag[i]
                        if (data != null) {
                            if (data.blockID != -1) {
                                pitch = -90f
                                yaw = 90f
                                delay(50)
                                client.sendPacket(ClickWindowPacket(0, i, 1, 1, ClickMode.DROP, data))
                                client.sendPacket(CCloseWindowPacket(0))
                            }
                            bag.remove(i)
                        }
                    }
                }
                pitch = 0f
            } catch (_: Exception) {
            }
        }


    }
    Thread.sleep(3000)
    while (true) {
        try {
            while (!client.connection.isClosed()) {
                Thread.sleep(50)
                if (yaw >= 360f) yaw = 0f
                else yaw += 0.5f

                if (yaw < 1f) client.sendPacket(EntityActionPacket(0, 1, 0))
                if (yaw > 359f) client.sendPacket(EntityActionPacket(0, 0, 0))

                val id = blocks.find { it.id == world.getOrSet(x, y - 1, z).id }?.name ?: "minecraft:air"
                val id1 = blocks.find { it.id == world.getOrSet(x, y - 2, z).id }?.name ?: "minecraft:air"

                if (id == "minecraft:air" || id.contains("torch", true)) {
                    if (id1 == "minecraft:air" || id.contains("torch", true)) {
                        client.sendPacket(
                            CPlayerPositionPacket(
                                x.toDouble() + 0.5,
                                y.toDouble() - 1,
                                z.toDouble() + 0.5,
                                false
                            )
                        )
                        y -= 1
                    } else {
                        client.sendPacket(
                            CPlayerPositionPacket(
                                x.toDouble() + 0.5,
                                y.toDouble() - 1,
                                z.toDouble() + 0.5,
                                false
                            )
                        )
                        y -= 1
                    }
                } else {
                    client.sendPacket(
                        CPlayerPositionAndLookPacket(
                            x.toDouble() + 0.5,
                            y.toDouble(),
                            z.toDouble() + 0.5,
                            yaw,
                            pitch,
                            true
                        )
                    )
                }

            }
        } catch (_: Exception) {
        }
    }
}
