package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readString

@Serializable
data class DisconnectPacket(
    val reason: String
) : Packet

class DisconnectDecoder : PacketDecoder<DisconnectPacket> {
    override fun decoder(buf: ByteBuf): DisconnectPacket {
        return DisconnectPacket(buf.readString(32767))
    }
}
