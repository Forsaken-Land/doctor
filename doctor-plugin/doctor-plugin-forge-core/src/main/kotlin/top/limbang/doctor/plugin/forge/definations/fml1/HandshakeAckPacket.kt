package top.limbang.doctor.plugin.forge.definations.fml1

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML1Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
@Serializable
data class HandshakeAckPacket(
    val discriminator: Byte = -1,
    val phase: Byte
) : FML1Packet

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