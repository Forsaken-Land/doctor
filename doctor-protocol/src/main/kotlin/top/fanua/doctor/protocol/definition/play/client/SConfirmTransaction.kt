package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:16:01
 */
@Serializable
data class SConfirmTransactionPacket(
    val windowId: Int,
    val actionNumber: Int,
    val accepted: Boolean
) : Packet

class SConfirmTransactionDecoder : PacketDecoder<SConfirmTransactionPacket> {
    override fun decoder(buf: ByteBuf): SConfirmTransactionPacket {
        val windowId = buf.readByte().toInt()
        val actionNumber = buf.readShort().toInt()
        val accepted = buf.readBoolean()
        return SConfirmTransactionPacket(windowId, actionNumber, accepted)
    }
}
