package top.limbang.doctor.protocol.definition.status.server

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 * ### 状态响应
 *
 * [json] 参见 服务器列表 Ping 响应，与所有字符串一样，这是以其长度作为前缀的VarInt类型变量
 */
@Serializable
data class ResponsePacket(
    val json: String
) : Packet

/**
 * ### 状态响应解码
 *
 * @see ResponsePacket
 */
class ResponseDecoder : PacketDecoder<ResponsePacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): ResponsePacket {
        return ResponsePacket(json = buf.readString())
    }
}

/**
 * ### 状态响应编码
 *
 * @see ResponsePacket
 */
class ResponseEncoder : PacketEncoder<ResponsePacket> {
    /**
     * 编码
     *
     * **服务器**
     */
    override fun encode(buf: ByteBuf, packet: ResponsePacket): ByteBuf {
        buf.writeString(packet.json)
        return buf
    }

}