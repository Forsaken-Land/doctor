package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder


@Serializable
data class BlockChangePacket(
    val blockPosition: Position,
    val blockId: Int
) : Packet

class BlockChangeDecoder : PacketDecoder<BlockChangePacket> {
    override fun decoder(buf: ByteBuf): BlockChangePacket {
        TODO()
//        return BlockChangePacket(buf.readBlockChange(), buf.readVarInt())
    }
}