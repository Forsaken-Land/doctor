package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/25:0:06
 */
data class UnloadChunkPacket(
    val chunkX: Int,
    val chunkY: Int
) : Packet

class UnloadChunkDecoder : PacketDecoder<UnloadChunkPacket> {
    override fun decoder(buf: ByteBuf): UnloadChunkPacket {
        val chunkX = buf.readInt()
        val chunkY = buf.readInt()
        return UnloadChunkPacket(chunkX, chunkY)
    }
}
