package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/22:19:24
 */
@Serializable
data class UseEntityPacket(
    val target: Int,
    val type: Int,
    val targetX: Float,
    val targetY: Float,
    val targetZ: Float,
    val hand: Int
) : Packet

class UseEntityEncoder : PacketEncoder<UseEntityPacket> {
    override fun encode(buf: ByteBuf, packet: UseEntityPacket): ByteBuf {
        buf.writeVarInt(packet.target)
        buf.writeVarInt(packet.type)
        if (packet.type != 2) return buf
        buf.writeFloat(packet.targetX)
        buf.writeFloat(packet.targetY)
        buf.writeFloat(packet.targetZ)
        buf.writeVarInt(packet.hand)
        return buf
    }
}
