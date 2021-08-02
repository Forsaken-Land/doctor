package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.writeVarInt

@Serializable
data class TeleportConfirmPacket(
    val teleportId: Int
) : Packet

class TeleportConfirmEncoder : PacketEncoder<TeleportConfirmPacket> {
    override fun encode(buf: ByteBuf, packet: TeleportConfirmPacket): ByteBuf {
        buf.writeVarInt(packet.teleportId)
        return buf
    }
}
