package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.io.core.Closeable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.writeString

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:36
 */
@Serializable
data class CustomPayloadPacket(
    val channel: String,
    @Contextual
    val data: ByteBuf,
    val processed: Boolean = false
) : Packet, Closeable {
    override fun close() {
        try {
            while (!data.release()) {
            }
        } catch (e: Exception) {
        }
    }

}

class CustomPayloadDecoder : PacketDecoder<CustomPayloadPacket> {
    override fun decoder(buf: ByteBuf): CustomPayloadPacket {
        val channel = buf.readString()
        val byteBuf = Unpooled.buffer(buf.readableBytes())
        buf.readBytes(byteBuf)
        return CustomPayloadPacket(channel, byteBuf)
    }
}

class CustomPayloadEncoder : PacketEncoder<CustomPayloadPacket> {
    override fun encode(buf: ByteBuf, packet: CustomPayloadPacket): ByteBuf {
        buf.writeString(packet.channel)
        buf.writeBytes(packet.data)
        packet.close()
        return buf
    }
}