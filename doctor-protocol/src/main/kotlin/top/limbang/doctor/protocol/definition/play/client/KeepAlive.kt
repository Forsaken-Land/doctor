package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 * ### 保持活动 (客户端)
 * 服务器会频繁地发送一个keep-alive，每个都包含一个随机的ID。客户端必须用相同的包响应。如果客户端在超过30秒的时间内没有响应，则服务器将启动客户端。反之亦然，如果服务器在20秒内未发送任何保持有效，则客户端将断开连接并产生“超时”异常。
 *
 * Notchian服务器使用依赖于系统的时间（毫秒）来生成keep-alive ID值。
 * - [keepAliveId] Keep Alive id
 */
@Serializable
data class CKeepAlivePacket(
    val keepAliveId: Long
) : Packet

/**
 * ### 保持活动 (服务器)
 * @see [CKeepAlivePacket]
 */
@Serializable
data class SKeepAlivePacket(
    val keepAliveId: Long
) : Packet

/**
 * ### 解码
 */
class KeepAliveDecoder : PacketDecoder<SKeepAlivePacket> {
    override fun decoder(buf: ByteBuf): SKeepAlivePacket {
        return SKeepAlivePacket(buf.readLong())
    }

}

/**
 * ### 编码
 */
class KeepAliveEncoder : PacketEncoder<CKeepAlivePacket> {
    override fun encode(buf: ByteBuf, packet: CKeepAlivePacket): ByteBuf {
        buf.writeLong(packet.keepAliveId)
        return buf
    }
}