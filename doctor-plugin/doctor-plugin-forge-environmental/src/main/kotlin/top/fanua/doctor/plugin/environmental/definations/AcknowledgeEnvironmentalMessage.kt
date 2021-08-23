package top.fanua.doctor.plugin.environmental.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/24:0:37
 */
data class AcknowledgeEnvironmentalMessagePacket(
    override var messageId: Int = 0
) : FML2Packet

class AcknowledgeEnvironmentalMessageEncoder : PacketEncoder<AcknowledgeEnvironmentalMessagePacket> {
    override fun encode(buf: ByteBuf, packet: AcknowledgeEnvironmentalMessagePacket) = buf
}
