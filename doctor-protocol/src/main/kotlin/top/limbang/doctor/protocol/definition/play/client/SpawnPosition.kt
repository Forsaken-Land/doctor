package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

@Serializable
data class SpawnPositionPacket(
    val location: Position
) : Packet

class SpawnPositionDecoder : PacketDecoder<SpawnPositionPacket> {
    override fun decoder(buf: ByteBuf): SpawnPositionPacket {
//        return SpawnPositionPacket(location = buf.readPosition())

        TODO()
    }
}