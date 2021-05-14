package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.api.plugin.ChannelPacket

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:36
 */
//TODO: 插件包解析
@Serializable
data class CustomPayloadPacket(
    val channel: String = "MC|Brand",
    val string: String
) : ChannelPacket

class CustomPayloadDecoder : PacketDecoder<CustomPayloadPacket> {
    override fun decoder(buf: ByteBuf): CustomPayloadPacket {
        //TODO 未处理
        val byteArray = ByteArray(buf.readableBytes())
        buf.readBytes(byteArray)
        return CustomPayloadPacket(string = String(byteArray))
    }

}

class CustomPayloadEncoder : PacketEncoder<CustomPayloadPacket> {
    override fun encode(buf: ByteBuf, packet: CustomPayloadPacket): ByteBuf {
        buf.writeBytes(packet.string.toByteArray())
        return buf
    }
}