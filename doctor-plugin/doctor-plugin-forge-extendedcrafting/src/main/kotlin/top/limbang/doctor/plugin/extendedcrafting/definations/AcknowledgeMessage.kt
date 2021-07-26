package top.limbang.doctor.plugin.extendedcrafting.definations

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:47
 */
data class AcknowledgeMessagePacket(
    override var messageId: Int = 0
) : FML2Packet

class AcknowledgeMessageDecoder : PacketDecoder<AcknowledgeMessagePacket> {
    override fun decoder(buf: ByteBuf): AcknowledgeMessagePacket {
        return AcknowledgeMessagePacket()
    }
}

class AcknowledgeMessageEncoder : PacketEncoder<AcknowledgeMessagePacket> {
    override fun encode(buf: ByteBuf, packet: AcknowledgeMessagePacket): ByteBuf {
        return buf
    }
}
