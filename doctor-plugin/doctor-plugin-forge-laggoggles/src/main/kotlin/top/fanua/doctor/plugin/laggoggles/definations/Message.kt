package top.fanua.doctor.plugin.laggoggles.definations

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.laggoggles.api.LagPacket
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readString

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
