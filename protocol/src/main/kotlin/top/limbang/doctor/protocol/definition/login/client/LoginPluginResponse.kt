package top.limbang.doctor.protocol.definition.login.client

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.io.core.Closeable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 * ### 登录插件响应
 *
 * - [messageId] 应与服务端的ID匹配
 * - [successful] true 表示客户端理解请求，false 则相反。当值为 false 时，没有有效载荷跟随。
 * - [data] 任何数据，取决于频道。必须从数据包长度推断出此数组的长度。
 */
@Serializable
data class LoginPluginResponsePacket(
    val messageId: Int,
    val successful: Boolean,
    @Contextual
    val data: ByteBuf
) : Packet, Closeable {
    override fun close() {
        try {
            while (!data.release()) {
            }
        } catch (e: Exception) {
        }
    }
}


/**
 * ### 登录插件响应编码
 *
 * @see LoginPluginResponsePacket
 */
class LoginPluginResponseEncoder : PacketEncoder<LoginPluginResponsePacket> {
    /**
     * 编码
     *
     * **客户端**
     */
    override fun encode(buf: ByteBuf, packet: LoginPluginResponsePacket): ByteBuf {
        buf.writeVarInt(packet.messageId)
        buf.writeBoolean(packet.successful)
        buf.writeBytes(packet.data)
        return buf
    }

}

/**
 * ### 登录插件响应解码
 *
 * @see LoginPluginResponsePacket
 */
class LoginPluginResponseDecoder : PacketDecoder<LoginPluginResponsePacket> {
    /**
     * 解码
     *
     * **服务器**
     */
    override fun decoder(buf: ByteBuf): LoginPluginResponsePacket {
        val messageId = buf.readVarInt()
        val successful = buf.readBoolean()
        val data = Unpooled.buffer(buf.readableBytes())
        buf.readBytes(data)
        return LoginPluginResponsePacket(messageId, successful, data)
    }
}