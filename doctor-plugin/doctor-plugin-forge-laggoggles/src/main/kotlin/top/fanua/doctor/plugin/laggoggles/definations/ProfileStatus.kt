package top.fanua.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.laggoggles.api.LagPacket
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readString

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/6:1:19
 */
@Serializable
data class ProfileStatusPacket(
    val isProfiling: Boolean,
    val length: Int,
    val issuedBy: String
) : LagPacket

class ProfileStatusDecoder : PacketDecoder<ProfileStatusPacket> {
    override fun decoder(buf: ByteBuf): ProfileStatusPacket {
        val isProfiling = buf.readBoolean()
        val length = buf.readInt()
        val issuedBy = buf.readString(length)
        return ProfileStatusPacket(isProfiling, length, issuedBy)
    }
}
