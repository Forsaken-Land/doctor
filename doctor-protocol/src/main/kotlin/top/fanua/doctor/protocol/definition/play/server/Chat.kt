package top.fanua.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.writeString

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
@Serializable
data class CChatPacket(
    val message: String
) : Packet

class CChatEncoder : PacketEncoder<CChatPacket> {
    override fun encode(buf: ByteBuf, packet: CChatPacket): ByteBuf {
        buf.writeString(packet.message)
        return buf
    }
}
