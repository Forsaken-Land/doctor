package top.fanua.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/3:3:25
 */
@Serializable
data class CConfirmTransactionPacket(
    val windowId: Int,
    val actionNumber: Int,
    val accepted: Boolean
) : Packet

class CConfirmTransactionEncoder : PacketEncoder<CConfirmTransactionPacket> {
    override fun encode(buf: ByteBuf, packet: CConfirmTransactionPacket): ByteBuf {
        buf.writeByte(packet.windowId)
        buf.writeShort(packet.actionNumber)
        buf.writeBoolean(packet.accepted)
        return buf
    }
}
