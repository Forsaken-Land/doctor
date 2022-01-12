package top.fanua.doctor.client.running.player.world

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.protocol.definition.play.client.BlockChangePacket
import top.fanua.doctor.protocol.definition.play.client.ChunkDataType0Packet
import top.fanua.doctor.protocol.entity.BlockState
import top.fanua.doctor.protocol.entity.World

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/12:13:39
 */
class PlayerWorldUtils(client: MinecraftClient) {
    val world = World()
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    init {

        client.onPacket<BlockChangePacket> {
            world.set(packet.blockPosition, BlockState(packet.blockId shr 4, packet.blockId and 15))
        }.onPacket<ChunkDataType0Packet> {
            if (packet.availableSections > 0) {
                world.chunks[Pair(packet.chunkX, packet.chunkZ)] = packet.chunk
            }
        }
    }
}
