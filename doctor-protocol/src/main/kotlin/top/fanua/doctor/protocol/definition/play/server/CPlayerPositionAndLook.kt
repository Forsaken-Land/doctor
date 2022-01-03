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
/**
 * A combination of Player Look and Player Position.
 * [x] Absolute position
 *
 * [y] Absolute feet position, normally Head Y + 1.62
 *
 * [z] Absolute position
 *
 * [yaw] Absolute rotation on the X Axis, in degrees
 *
 * [pitch] Absolute rotation on the Y Axis, in degrees
 *
 * [onGround] True if the client is on the ground, false otherwise
 *
 * Updates the direction the player is looking in.
 *
 * [yaw] is measured in degrees, and does not follow classical trigonometry rules. The unit circle of yaw on the XZ-plane starts at (0, 1) and turns counterclockwise, with 90 at (-1, 0), 180 at (0,-1) and 270 at (1, 0). Additionally, yaw is not clamped to between 0 and 360 degrees; any number is valid, including negative numbers and numbers greater than 360.
 *
 * [pitch] is measured in degrees, where 0 is looking straight ahead, -90 is looking straight up, and 90 is looking straight down.
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
