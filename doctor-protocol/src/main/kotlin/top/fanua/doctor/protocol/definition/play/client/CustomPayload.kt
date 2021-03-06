package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.io.core.Closeable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.readVarShort
import top.fanua.doctor.protocol.extension.writeString

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

class CustomPayloadBeforeDecoder : PacketDecoder<CustomPayloadPacket> {
    override fun decoder(buf: ByteBuf): CustomPayloadPacket {
        val channel = buf.readString()
        val byteBuf = Unpooled.buffer(buf.readVarShort())
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

class CustomPayloadBeforeEncoder : PacketEncoder<CustomPayloadPacket> {
    override fun encode(buf: ByteBuf, packet: CustomPayloadPacket): ByteBuf {
        buf.writeString(packet.channel)
        buf.writeShort(packet.data.readableBytes())
        buf.writeBytes(packet.data)
        packet.close()
        return buf
    }
}
