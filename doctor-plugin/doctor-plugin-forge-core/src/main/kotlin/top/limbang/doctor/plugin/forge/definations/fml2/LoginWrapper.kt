package top.limbang.doctor.plugin.forge.definations.fml2

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.io.core.Closeable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.writeString

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/30:16:09
 */
@Serializable
data class LoginWrapperPacket(
    val resourceLocation: String,
    @Contextual
    val data: ByteBuf,
    override var messageId: Int = 0
) : FML2Packet, Closeable {
    override fun close() {
        try {
            while (!data.release()) {
            }
        } catch (e: Exception) {
        }
    }
}


class LoginWrapperDecoder : PacketDecoder<LoginWrapperPacket> {
    override fun decoder(buf: ByteBuf): LoginWrapperPacket {
        val resourceLocation = buf.readString()
        val string = Unpooled.buffer(buf.readableBytes())
        buf.readBytes(string)
        return LoginWrapperPacket(resourceLocation, string)
    }
}

class LoginWrapperEncoder : PacketEncoder<LoginWrapperPacket> {
    override fun encode(buf: ByteBuf, packet: LoginWrapperPacket): ByteBuf {
        buf.writeString(packet.resourceLocation)
        buf.writeBytes(packet.data)
        return buf
    }

}
