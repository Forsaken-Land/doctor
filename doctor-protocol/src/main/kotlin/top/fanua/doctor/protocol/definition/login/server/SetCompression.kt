package top.fanua.doctor.protocol.definition.login.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.readVarInt
import top.fanua.doctor.protocol.extension.writeVarInt

/**
 * ### 设置压缩
 * 负值或零值将禁用压缩，这个包是完全可选的，如果不发送，压缩也不会被启用。
 *
 * [threshold] 压缩阈值
 */
@Serializable
data class SetCompressionPacket(
    val threshold: Int
) : Packet

/**
 * ### 设置压缩解码
 *
 * @see SetCompressionPacket
 */
class SetCompressionDecoder : PacketDecoder<SetCompressionPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): SetCompressionPacket {
        return SetCompressionPacket(threshold = buf.readVarInt())
    }
}

/**
 * ### 设置压缩编码
 *
 * @see SetCompressionPacket
 */
class SetCompressionEncode : PacketEncoder<SetCompressionPacket> {
    /**
     * 编码
     *
     * **服务器**
     */
    override fun encode(buf: ByteBuf, packet: SetCompressionPacket): ByteBuf {
        buf.writeVarInt(packet.threshold)
        return buf
    }
}
