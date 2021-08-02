package top.fanua.doctor.protocol.definition.status.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 * ### 状态请求
 *
 */
@Serializable
class RequestPacket : Packet

/**
 * ### 状态请求协议包编码
 * 无数据
 */
class RequestEncoder : PacketEncoder<RequestPacket> {

    /**
     * ### 编码 客户端
     */
    override fun encode(buf: ByteBuf, packet: RequestPacket): ByteBuf {
        return buf
    }

}

/**
 * ### 状态请求协议包解码
 * 无数据
 */
class RequestDecoder : PacketDecoder<RequestPacket> {

    /**
     * ### 解码 服务器
     */
    override fun decoder(buf: ByteBuf): RequestPacket {
        return RequestPacket()
    }
}
