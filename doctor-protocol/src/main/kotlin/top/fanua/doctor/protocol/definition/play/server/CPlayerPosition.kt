package top.fanua.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/25:1:02
 */
@Serializable
data class CPlayerPositionPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val onGround: Boolean
) : Packet

class CPlayerPositionEncoder : PacketEncoder<CPlayerPositionPacket> {
    override fun encode(buf: ByteBuf, packet: CPlayerPositionPacket): ByteBuf {
        buf.writeDouble(packet.x)
        buf.writeDouble(packet.y)
        buf.writeDouble(packet.z)
        buf.writeBoolean(packet.onGround)
        return buf
    }
}
