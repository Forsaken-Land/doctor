package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.api.plugin.ChannelPacket
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.writeString

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:36
 */
@Serializable
data class CustomPayloadPacket(
    val channel: String = "MC|Brand",
    @Contextual
    val data: ByteBuf
) : ChannelPacket

class CustomPayloadDecoder : PacketDecoder<CustomPayloadPacket> {
    override fun decoder(buf: ByteBuf): CustomPayloadPacket {
        val channel = buf.readString()
        val byteBuf = buf.readBytes(buf.readableBytes())
        return CustomPayloadPacket(channel, byteBuf)
    }

}

class CustomPayloadEncoder : PacketEncoder<CustomPayloadPacket> {
    override fun encode(buf: ByteBuf, packet: CustomPayloadPacket): ByteBuf {
        buf.writeString(packet.channel)
        synchronized(packet.data) {
            packet.data.markReaderIndex()
            buf.writeBytes(packet.data)
            packet.data.resetReaderIndex()
        }
        return buf
    }
}