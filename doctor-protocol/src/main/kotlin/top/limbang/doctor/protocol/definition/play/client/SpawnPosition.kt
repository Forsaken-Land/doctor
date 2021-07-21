package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

@Serializable
data class SpawnPositionPacket(
    val x: Int,
    val y: Int,
    val z: Int
) : Packet

class SpawnPositionDecoder : PacketDecoder<SpawnPositionPacket> {
    override fun decoder(buf: ByteBuf): SpawnPositionPacket {
        val x = buf.readInt()
        val y = buf.readInt()
        val z = buf.readInt()
        return SpawnPositionPacket(x, y, z)
    }
}
