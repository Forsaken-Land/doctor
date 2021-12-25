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
import top.fanua.doctor.protocol.definition.play.client.ChatPacket
import top.fanua.doctor.protocol.definition.play.client.ChunkDataType0Packet
import top.fanua.doctor.protocol.definition.play.client.DisconnectPacket
import top.fanua.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionAndLookPacket
import top.fanua.doctor.protocol.entity.*
import top.fanua.doctor.protocol.entity.text.ChatSerializer
import kotlin.math.abs
import kotlin.math.pow


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
        if (packet.name == "minecraft:blocks") ids.addAll(packet.ids)
    }
        .onPacket<ChatPacket> {
            if (!packet.json.contains("commands.forge.tps.summary")) {
//            logger.info(packet.json)
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
//                logger.info("chunk:${Chunk(packet.chunkX, packet.chunkZ, list)}")
                if (chunks.find { it.chunkZ == packet.chunkZ && it.chunkX == packet.chunkX } == null) {
                    chunks.add(Chunk(packet.chunkX, packet.chunkZ, list))
                }
                buf.release()
            }
        }
//        .onPacket<PlayerPositionAndLookPacket> {
//        logger.info("登录成功")
//        client.sendPacket(CPlayerPositionPacket(packet.x, packet.y, packet.z, false))
//    }
//    .onPacket<STabCompletePacket> {
//        logger.info("tab")
//    }
    val job = GlobalScope.launch {
        while (true) {
            when (val msg = readLine()) {
                "1" -> {
                    getUnder(x, y, z, chunks, ids)
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

//    var entity= 0
////    var x = 0.0
////    var y = 0.0
////    var z = 0.0
    Thread.sleep(3000)
    while (true) {
        Thread.sleep(10)
        if (getUnder(x, y, z, chunks, ids) == "minecraft:air") {
            if (getUnder(x, y - 1, z, chunks, ids) == "minecraft:air") {
                val addX = (-2..2).random()
                val addZ = (-2..2).random()
                client.sendPacket(
                    CPlayerPositionAndLookPacket(
                        x.toDouble() + addX,
                        y - 1.toDouble(),
                        z.toDouble() + addZ,
                        (0..360).random().toFloat(),
                        (-90..90).random().toFloat(),
                        false
                    )
                )
                y -= 1
                x += addX
                z += addZ
            } else {
                val addX = (-2..2).random()
                val addZ = (-2..2).random()
                client.sendPacket(
                    CPlayerPositionAndLookPacket(
                        x.toDouble() + addX,
                        y - 1.toDouble(),
                        z.toDouble() + addZ,
                        (0..360).random().toFloat(),
                        (-90..90).random().toFloat(),
                        true
                    )
                )
                y -= 1
                x += addX
                z += addZ
            }
        }
    }
////    client.onPacket<PlayerPositionAndLookPacket> {
////        x = packet.x
////        y = packet.y
////        z = packet.z
////    }
//    client.sendPacket(EntityActionPacket(entity, 0, 0))
//    val time = 100L
////    val size = 2
//    while (true) {
//        client.sendPacket(EntityActionPacket(entity, 0, 0))
////        client.sendPacket(CPlayerPositionAndLookPacket(x + size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
//        Thread.sleep(time)
//        client.sendPacket(EntityActionPacket(entity, 1, 0))
//        Thread.sleep(time)
//
////        client.sendPacket(CPlayerPositionAndLookPacket(x + size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
////        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z + size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
////        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z + size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
////        client.sendPacket(CPlayerPositionAndLookPacket(x - size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
////        client.sendPacket(CPlayerPositionAndLookPacket(x - size, y, z, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
////        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z - size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
////        client.sendPacket(CPlayerPositionAndLookPacket(x, y, z - size, (0..360).random().toFloat(), (-90..90).random().toFloat(), true))
////        Thread.sleep(time)
//    }


}

private fun getUnder(
    x: Int,
    y: Int,
    z: Int,
    chunks: MutableList<Chunk>,
    ids: MutableList<Ids>
): String {
    val chunkX = if (x / 16 >= 0) x / 16
    else (x / 16) - 1
    val chunkY = if (y / 16 >= 0) y / 16
    else (y / 16) - 1
    val chunkZ = if (z / 16 >= 0) z / 16
    else (z / 16) - 1
    val chunk = chunks.find { it.chunkX == chunkX && it.chunkZ == chunkZ }
    val section = chunk?.section?.get(chunkY)
    if (section != null) {
//        logger.info("chunk:$chunkX,$chunkY,$chunkZ")
        val bit = section.blocks.bitsPerEntry
        var data = ""
        section.blocks.flexibleStorage.data.forEach {
            val str = it.toULong().toString(2)
            var temp = ""
            for (i in 0 until (64 - str.length)) {
                temp += "0"
            }
            data += temp + str
        }
        val l = mutableListOf<String>()
        for (i in 0 until data.length / bit) {
            val j = data.substring(i * bit, (i * bit) + bit)
            var binaryNumber = j.toLong()
            var decimalNo = 0
            var power = 0
            while (binaryNumber > 0) {
                val r = binaryNumber % 10
                decimalNo = (decimalNo + r * 2.0.pow(power.toDouble())).toInt()
                binaryNumber /= 10
                power++
            }
            val name = ids.find { it.id == decimalNo }?.name ?: "air"
            l.add(name)
        }
//        logger.info(
//            "${
//                if (x > 0) abs(x % 16)
//                else 16 - abs(x % 16)
//            }, ${abs(y) % 16 - 1}, ${
//                if (z > 0) abs(z % 16)
//                else 16 - abs(z % 16)
//            }"
//        )
//        logger.info(
//            l[index(
//                if (x > 0) abs(x % 16)
//                else 16 - abs(x % 16), abs(y) % 16 - 1, if (z > 0) abs(z % 16)
//                else 16 - abs(z % 16)
//            )]
//        )
//                        logger.info(l.toString())
        return l[abs(
            index(
                if (x > 0) abs(x % 16)
                else 16 - abs(x % 16), abs(y) % 16 - 1, if (z > 0) abs(z % 16)
                else 16 - abs(z % 16)
            )
        )]
    } else return "minecraft:air"
}
