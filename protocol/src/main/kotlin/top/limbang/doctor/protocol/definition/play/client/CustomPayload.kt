package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.entity.extra.CustomPayloadType
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
    val data: Map<String, @Contextual Any>,
    @Contextual
    val rawData: ByteBuf? = null,
    val processed: Boolean = false
) : Packet

class CustomPayloadDecoder : PacketDecoder<CustomPayloadPacket> {
    override fun decoder(buf: ByteBuf): CustomPayloadPacket {
        val channel = buf.readString()
        val byteBuf = buf.readBytes(buf.readableBytes())
        val data = HashMap<String, Any>()
        val type = CustomPayloadType.get(channel)
        if (type != CustomPayloadType.UNKNOWN) {
            type.readPacket(byteBuf, data)
            return CustomPayloadPacket(channel, data, processed = true)
        }
        return CustomPayloadPacket(channel, emptyMap(), byteBuf)
    }

}

class CustomPayloadEncoder : PacketEncoder<CustomPayloadPacket> {
    override fun encode(buf: ByteBuf, packet: CustomPayloadPacket): ByteBuf {
        buf.writeString(packet.channel)
        val data = Unpooled.buffer()
        CustomPayloadType.get(packet.channel).writePacket(data, packet.data)
        buf.writeBytes(data)
        return buf
    }
}