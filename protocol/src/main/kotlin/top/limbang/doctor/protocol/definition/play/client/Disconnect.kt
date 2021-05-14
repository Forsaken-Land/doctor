package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

@Serializable
data class DisconnectPacket(
    val reason: String
) : Packet

class DisconnectDecoder : PacketDecoder<DisconnectPacket> {
    override fun decoder(buf: ByteBuf): DisconnectPacket {
        return DisconnectPacket(buf.readString(32767))
    }
}