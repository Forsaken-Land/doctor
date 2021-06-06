package top.limbang.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.laggoggles.api.LagPacket
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.extension.readString

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/6:11:22
 */
@Serializable
data class MessagePacket(
    val message: String,
    val seconds: Int
) : LagPacket

class MessageDecoder : PacketDecoder<MessagePacket> {
    override fun decoder(buf: ByteBuf): MessagePacket {
        val message = buf.readString()
        val seconds = buf.readInt()
        return MessagePacket(message, seconds)
    }
}
