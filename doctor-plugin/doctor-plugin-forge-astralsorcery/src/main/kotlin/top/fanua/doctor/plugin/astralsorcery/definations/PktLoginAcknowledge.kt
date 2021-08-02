package top.fanua.doctor.plugin.astralsorcery.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:9:18
 */
data class PktLoginAcknowledgePacket(
    override var messageId: Int = 0
) : FML2Packet

class PktLoginAcknowledgeDecoder : PacketDecoder<PktLoginAcknowledgePacket> {
    override fun decoder(buf: ByteBuf): PktLoginAcknowledgePacket {
        return PktLoginAcknowledgePacket()
    }
}

class PktLoginAcknowledgeEncoder : PacketEncoder<PktLoginAcknowledgePacket> {
    override fun encode(buf: ByteBuf, packet: PktLoginAcknowledgePacket): ByteBuf {
        return buf
    }
}
