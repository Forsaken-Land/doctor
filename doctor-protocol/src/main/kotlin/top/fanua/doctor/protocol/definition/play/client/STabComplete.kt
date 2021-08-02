package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.core.annotation.VersionExpandPacket
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.readVarInt

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
interface STabCompletePacket : Packet

@Serializable
@VersionExpandPacket(STabCompletePacket::class)
data class STabCompleteType1Packet(
    val id: Int,
    val start: Int,
    val length: Int,
    val count: Int,
    val matches: List<Matches>
) : STabCompletePacket {
    @Serializable
    data class Matches(
        val match: String,
        val hasTooltip: Boolean,
        val tooltip: String?
    )

}


@Serializable
@VersionExpandPacket(STabCompletePacket::class)
data class STabCompleteType0Packet(
    val matches: Array<String>
) : STabCompletePacket {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as STabCompleteType0Packet

        if (!matches.contentEquals(other.matches)) return false

        return true
    }

    override fun hashCode(): Int {
        return matches.contentHashCode()
    }

}

@Serializable
@VersionExpandPacket(STabCompletePacket::class)
data class STabCompleteType2Packet(
    val count: Int,
    val match: String
) : STabCompletePacket

class STabCompleteType0Decoder : PacketDecoder<STabCompleteType0Packet> {
    override fun decoder(buf: ByteBuf): STabCompleteType0Packet {
        val length = buf.readVarInt()
        val matches = Array(length) { "" }
        for (i in 0 until length) {
            matches[i] = buf.readString()
        }
        return STabCompleteType0Packet(
            matches
        )
    }

}

class STabCompleteType1Decoder : PacketDecoder<STabCompleteType1Packet> {
    override fun decoder(buf: ByteBuf): STabCompleteType1Packet {
        val id = buf.readVarInt()
        val start = buf.readVarInt()
        val length = buf.readVarInt()
        val count = buf.readVarInt()
        val matches = mutableListOf<STabCompleteType1Packet.Matches>()
        for (i in 0 until count) {
            val match = buf.readString()
            val hasTooltip = buf.readBoolean()
            val tooltip = if (hasTooltip) {
                buf.readString()
            } else {
                null
            }
            matches.add(STabCompleteType1Packet.Matches(match, hasTooltip, tooltip))
        }
        return STabCompleteType1Packet(
            id, start, length, count, matches
        )
    }

}

class STabCompleteType2Decoder : PacketDecoder<STabCompleteType2Packet> {
    override fun decoder(buf: ByteBuf): STabCompleteType2Packet {
        val count = buf.readVarInt()
        val match = buf.readString()
        return STabCompleteType2Packet(count, match)
    }
}
