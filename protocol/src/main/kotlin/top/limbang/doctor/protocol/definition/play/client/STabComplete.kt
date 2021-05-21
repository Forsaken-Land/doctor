package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readVarInt

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
@Serializable
data class STabCompletePacket(
    val matches: Array<String>
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is STabCompletePacket) return false

        if (!matches.contentEquals(other.matches)) return false

        return true
    }

    override fun hashCode(): Int {
        return matches.contentHashCode()
    }
}

class STabCompleteDecoder : PacketDecoder<STabCompletePacket> {
    override fun decoder(buf: ByteBuf): STabCompletePacket {
        val length = buf.readVarInt()
        val matches = Array(length) { "" }
        for (i in 0 until length) {
            matches[i] = buf.readString()
        }
        return STabCompletePacket(
            matches
        )
    }

}
