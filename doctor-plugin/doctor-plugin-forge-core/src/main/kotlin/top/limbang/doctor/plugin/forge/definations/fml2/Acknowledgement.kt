package top.limbang.doctor.plugin.forge.definations.fml2

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/1 下午9:44
 */
@Serializable
data class AcknowledgementPacket(
    override var messageId: Int
) : FML2Packet

class AcknowledgementEncoder : PacketEncoder<AcknowledgementPacket> {
    override fun encode(buf: ByteBuf, packet: AcknowledgementPacket): ByteBuf {
        return buf
    }
}