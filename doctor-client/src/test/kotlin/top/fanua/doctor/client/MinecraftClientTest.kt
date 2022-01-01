package top.fanua.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.fanua.doctor.allLoginPlugin.enableAllLoginPlugin
import top.fanua.doctor.client.running.*
import top.fanua.doctor.client.running.tabcomplete.TabCompletePlugin
import top.fanua.doctor.client.running.tabcomplete.tabCompleteTool
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.forge.definations.fml1.Ids
import top.fanua.doctor.plugin.forge.definations.fml1.RegistryDataPacket
import top.fanua.doctor.protocol.definition.play.client.*
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionAndLookPacket
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionPacket
import top.fanua.doctor.protocol.entity.BlockState
import top.fanua.doctor.protocol.entity.Chunk
import top.fanua.doctor.protocol.entity.Section
import top.fanua.doctor.protocol.entity.text.ChatSerializer
import java.math.RoundingMode
import kotlin.math.abs


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
    val chunks = mutableListOf<Chunk>()

    val ids = mutableListOf<Ids>()
    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(1000L)
        client.reconnect()
    }.onPacket<RegistryDataPacket> {
        if (packet.name == "minecraft:blocks") {
            ids.addAll(packet.ids)
        }
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
        }.onPacket<BlockChangePacket> {
            val section = getSection(packet.blockPosition, chunks)
            if (section != null) {
                section.blocks[
                        if (packet.blockPosition.x > 0) abs(packet.blockPosition.x % 16) else abs(packet.blockPosition.x % 16) + 1,
                        abs(packet.blockPosition.y) % 16,
                        if (packet.blockPosition.z > 0) abs(packet.blockPosition.z % 16) else abs(packet.blockPosition.z % 16) + 1
                ] = BlockState(packet.blockId shr 4, packet.blockId and 15)
                val chunkX = if (packet.blockPosition.x / 16 >= 0) packet.blockPosition.x / 16
                else (packet.blockPosition.x / 16) - 1
                val chunkY = if (packet.blockPosition.y / 16 >= 0) packet.blockPosition.y / 16
                else (packet.blockPosition.y / 16) - 1
                val chunkZ = if (packet.blockPosition.z / 16 >= 0) packet.blockPosition.z / 16
                else (packet.blockPosition.z / 16) - 1
                val chunk = chunks.find { it.chunkX == chunkX && it.chunkZ == chunkZ }
                if (chunk != null) {
                    chunks.remove(chunk)
                    chunk.section[chunkY] = section
                    chunks.add(chunk)
                }
            }

//            if ((packet.blockPosition.x in x - 2..x + 2) &&
//                (packet.blockPosition.y in y - 2..y + 2) &&
//                (packet.blockPosition.z in z - 2..z + 2)
//            ) {
//                val data = getSection(packet.blockPosition, chunks)
//                if (data != null) {
//                    logger.info(
//                        data.blocks[
//                                if (packet.blockPosition.x > 0) abs(packet.blockPosition.x % 16) else abs(packet.blockPosition.x % 16) + 1,
//                                abs(packet.blockPosition.y) % 16,
//                                if (packet.blockPosition.z > 0) abs(packet.blockPosition.z % 16) else +abs(packet.blockPosition.z % 16) + 1
//                        ].toString()
//                    )
//                }
//                logger.info((packet.blockId shr 4).toString())
//            }
        }
        .onPacket<ChunkDataType0Packet> {
            if (packet.availableSections > 0) {
                val data = chunks.find { it.chunkZ == packet.chunkZ && it.chunkX == packet.chunkX }
                if (data == null) {
                    chunks.add(packet.chunk)
                } else {
                    chunks.remove(data)
                    chunks.add(packet.chunk)
                }

            }
        }

    val job = GlobalScope.launch {
        while (true) {
            when (val msg = readLine()) {
                "1" -> {
                    val under = getSection(x, y - 1, z, chunks)
                    val blockState = under?.blocks?.get(
                        if (x > 0) abs(x % 16) else abs(x % 16) + 1,
                        abs(y - 1) % 16,
                        if (z > 0) abs(z % 16) else abs(z % 16) + 1
                    )
                    val id = ids.find { it.id == blockState?.id }?.name
                    logger.info(id)
                    logger.info("x:$x,y:$y,z:$z")
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
                        } catch (e: Exception) {
                        }
                    }
                    if (!msg.isNullOrBlank()) {
                        client.sendMessage(msg)
                    }
                }
            }

        }
    }

    Thread.sleep(3000)
    while (true) {
        Thread.sleep(20)
        try {
            client.sendPacket(EntityActionPacket(0, (0..1).random(), 0))
            val under = getSection(x, y - 1, z, chunks)
            val under1 = getSection(x, y, z - 2, chunks)
            val blockState = under?.blocks?.get(
                if (x > 0) abs(x % 16) else abs(x % 16) + 1,
                abs(y - 1) % 16,
                if (z > 0) abs(z % 16) else abs(z % 16) + 1
            )
            val blockState1 = under1?.blocks?.get(
                if (x > 0) abs(x % 16) else abs(x % 16) + 1,
                abs(y - 2) % 16,
                if (z > 0) abs(z % 16) else abs(z % 16) + 1
            )
            val id = ids.find { it.id == blockState?.id }?.name ?: "minecraft:air"
            val id1 = ids.find { it.id == blockState1?.id }?.name ?: "minecraft:air"


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
                        160f,
                        160f,
                        true
                    )
                )
            }
        } catch (e: Exception) {
        }

    }


}

private fun getSection(
    x: Int,
    y: Int,
    z: Int,
    chunks: MutableList<Chunk>,
): Section? {
    val chunkX = if (x / 16 >= 0) x / 16
    else (x / 16) - 1
    val chunkY = if (y / 16 >= 0) y / 16
    else (y / 16) - 1
    val chunkZ = if (z / 16 >= 0) z / 16
    else (z / 16) - 1
    val chunk = chunks.find { it.chunkX == chunkX && it.chunkZ == chunkZ }
    return chunk?.section?.get(chunkY)
}

private fun getSection(position: Position, chunks: MutableList<Chunk>) =
    getSection(position.x, position.y, position.z, chunks)
