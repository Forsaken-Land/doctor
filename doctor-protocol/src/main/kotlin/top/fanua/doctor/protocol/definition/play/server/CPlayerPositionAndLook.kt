package top.fanua.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/23:13:59
 */
@Serializable
data class CPlayerPositionAndLookPacket(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
    val onGround: Boolean
) : Packet

class CPlayerPositionAndLookEncoder : PacketEncoder<CPlayerPositionAndLookPacket> {
    override fun encode(buf: ByteBuf, packet: CPlayerPositionAndLookPacket): ByteBuf {
        buf.writeDouble(packet.x)
        buf.writeDouble(packet.y)
        buf.writeDouble(packet.z)
        buf.writeFloat(packet.yaw)
        buf.writeFloat(packet.pitch)
        buf.writeBoolean(packet.onGround)
        return buf
    }
}
