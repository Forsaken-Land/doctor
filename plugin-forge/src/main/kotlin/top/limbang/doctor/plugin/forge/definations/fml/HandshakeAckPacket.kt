package top.limbang.doctor.plugin.forge.definations.fml

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.plugin.forge.api.ChannelPacket

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
@Serializable
data class HandshakeAckPacket(
    val discriminator: Byte = -1,
    val phase: Byte
) : ChannelPacket

class HandshakeAckDecoder : PacketDecoder<HandshakeAckPacket> {
    override fun decoder(buf: ByteBuf): HandshakeAckPacket {
        val discriminator = buf.readByte()
        val phase = buf.readByte()
        return HandshakeAckPacket(discriminator = discriminator, phase = phase)
    }

}

class HandshakeAckEncoder : PacketEncoder<HandshakeAckPacket> {
    override fun encode(buf: ByteBuf, packet: HandshakeAckPacket): ByteBuf {
        buf.writeByte(packet.discriminator.toInt())
        buf.writeByte(packet.discriminator.toInt())
        return buf
    }

}