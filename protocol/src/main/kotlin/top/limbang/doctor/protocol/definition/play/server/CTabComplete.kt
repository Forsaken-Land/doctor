package top.limbang.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import jdk.nashorn.internal.objects.NativeString.substring
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.entity.math.BlockPos
import top.limbang.doctor.protocol.extension.writeBlockPos
import top.limbang.doctor.protocol.extension.writeString

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
@Serializable
data class CTabCompletePacket(
    var message: String,
    val hasTargetBlock: Boolean = false,
    val targetBlock: BlockPos? = null
) : Packet

class CTabCompleteEncoder : PacketEncoder<CTabCompletePacket> {
    override fun encode(buf: ByteBuf, packet: CTabCompletePacket): ByteBuf {
        buf.writeString(substring(packet.message, 0, 32767))
        buf.writeBoolean(packet.hasTargetBlock)
        val flag = packet.targetBlock != null
        buf.writeBoolean(flag)
        if (flag) {
            buf.writeBlockPos(packet.targetBlock!!)
        }
        return buf
    }
}
