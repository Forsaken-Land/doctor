package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder

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