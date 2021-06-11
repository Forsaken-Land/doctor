package top.limbang.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.entity.math.BlockPos
import top.limbang.doctor.protocol.extension.writeBlockPos
import top.limbang.doctor.protocol.extension.writeString
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
interface CTabCompletePacket : Packet {
    val text: String
}

@Serializable
data class CTabCompleteType0Packet(
    override var text: String,
    val hasTargetBlock: Boolean = false,
    val targetBlock: BlockPos? = null
) : CTabCompletePacket

@Serializable
data class CTabCompleteType1Packet(
    val transactionId: Int = 0,
    override val text: String
) : CTabCompletePacket

class CTabCompleteType0Encoder : PacketEncoder<CTabCompleteType0Packet> {
    override fun encode(buf: ByteBuf, packet: CTabCompleteType0Packet): ByteBuf {
        buf.writeString(packet.text.substring(0, 32767))
        buf.writeBoolean(packet.hasTargetBlock)
        val flag = packet.targetBlock != null
        buf.writeBoolean(flag)
        if (flag) {
            buf.writeBlockPos(packet.targetBlock!!)
        }
        return buf
    }
}

class CTabCompleteType1Encoder : PacketEncoder<CTabCompleteType1Packet> {
    override fun encode(buf: ByteBuf, packet: CTabCompleteType1Packet): ByteBuf {
        return buf.also {
            it.writeVarInt(packet.transactionId)
            it.writeString(packet.text)
        }
    }
}
