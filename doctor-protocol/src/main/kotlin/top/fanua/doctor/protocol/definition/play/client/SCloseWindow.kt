package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:15:59
 */
@Serializable
data class SCloseWindowPacket(val windowId: Int) : Packet

class SCloseWindowDecoder : PacketDecoder<SCloseWindowPacket> {
    override fun decoder(buf: ByteBuf): SCloseWindowPacket {
        return SCloseWindowPacket(buf.readUnsignedByte().toInt())
    }
}
