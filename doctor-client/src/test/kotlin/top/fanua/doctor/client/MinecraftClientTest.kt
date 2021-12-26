package top.fanua.doctor.client

import io.netty.buffer.Unpooled
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
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionPacket
import top.fanua.doctor.protocol.entity.BlockStorage
import top.fanua.doctor.protocol.entity.Chunk
import top.fanua.doctor.protocol.entity.NibbleArray3d
import top.fanua.doctor.protocol.entity.Section
import top.fanua.doctor.protocol.entity.text.ChatSerializer
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
            x = packet.x.toInt()
            y = packet.y.toInt()
            z = packet.z.toInt()
        }
        .onPacket<ChunkDataType0Packet> {
            if (packet.availableSections > 0) {
                val buf = Unpooled.wrappedBuffer(packet.buffer)
                val list = mutableMapOf<Int, Section?>()
                for (i in 0 until 16) {
                    if (packet.availableSections and (1 shl i) == 0) {
                        list[i] = null
                    } else {
                        val blocks = BlockStorage(buf.readUnsignedByte().toInt(), buf)
                        val blockLight = NibbleArray3d(buf)
                        val skyLight = NibbleArray3d(buf)
                        list[i] = Section(i, blocks, blockLight, skyLight)
                    }
                }
                if (packet.fullChunk) {
                    val blockBiomesArray = ByteArray(256)
                    buf.readBytes(blockBiomesArray)
                }
                if (chunks.find { it.chunkZ == packet.chunkZ && it.chunkX == packet.chunkX } == null) {
                    chunks.add(Chunk(packet.chunkX, packet.chunkZ, list))
                }
                buf.release()
            }
        }

    val job = GlobalScope.launch {
        while (true) {
            when (val msg = readLine()) {
                "1" -> {
                    println(getUnder(x, y, z, chunks, ids))
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
        try {
            Thread.sleep(20)
            client.sendPacket(EntityActionPacket(0, (0..1).random(), 0))
            if (getUnder(x, y, z, chunks, ids).first == "minecraft:air") {
                if (getUnder(x, y - 1, z, chunks, ids).first == "minecraft:air") {
                    client.sendPacket(
                        CPlayerPositionPacket(
                            x.toDouble(),
                            y.toDouble() - 1,
                            z.toDouble(),
                            false
                        )
                    )
                    y -= 1
                } else {
                    client.sendPacket(
                        CPlayerPositionPacket(
                            x.toDouble(),
                            y.toDouble() - 1,
                            z.toDouble(),
                            true
                        )
                    )
                    y -= 1
                }
            } else {

                client.sendPacket(
                    CPlayerPositionPacket(
                        x.toDouble(),
                        y.toDouble(),
                        z.toDouble(),
                        true
                    )
                )
            }
        } catch (e: Exception) {
            println(e)
        }

    }


}

private fun getUnder(
    x: Int,
    y: Int,
    z: Int,
    chunks: MutableList<Chunk>,
    ids: MutableList<Ids>
): Pair<String, Int> {
    val chunkX = if (x / 16 >= 0) x / 16
    else (x / 16) - 1
    val chunkY = if (y / 16 >= 0) y / 16
    else (y / 16) - 1
    val chunkZ = if (z / 16 >= 0) z / 16
    else (z / 16) - 1
    val chunk = chunks.find { it.chunkX == chunkX && it.chunkZ == chunkZ }
    val section = chunk?.section?.get(chunkY)
    return if (section != null) {
        val id = section.blocks[if (x > 0) abs(x % 16)
        else 16 - abs(x % 16), abs(y) % 16 - 1, if (z > 0) abs(z % 16)
        else 16 - abs(z % 16)]
        val name = ids.find {
            it.id == id.id
        }?.name ?: "minecraft:air"
        (Pair(name, id.data))
    } else (Pair("minecraft:air", 0))
}
