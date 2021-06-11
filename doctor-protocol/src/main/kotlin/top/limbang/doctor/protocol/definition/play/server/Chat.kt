package top.limbang.doctor.protocol.definition.play.server

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder

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
