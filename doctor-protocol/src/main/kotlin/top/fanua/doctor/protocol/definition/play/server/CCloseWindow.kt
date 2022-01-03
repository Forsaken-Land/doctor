package top.fanua.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:15:12
 */
@Serializable
data class CCloseWindowPacket(
    val windowId: Int
) : Packet

class CCloseWindowEncoder : PacketEncoder<CCloseWindowPacket> {
    override fun encode(buf: ByteBuf, packet: CCloseWindowPacket): ByteBuf {
        buf.writeByte(packet.windowId and 0xFF)
        return buf
    }
}
