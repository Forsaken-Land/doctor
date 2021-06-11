package top.limbang.doctor.protocol.definition.login.server

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.io.core.Closeable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.extension.writeString
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 * ### 登录插件请求
 * 用于与 登录插件应答 一起实现自定义握手流。
 * [messageId] 由服务端生成（对于连接来说应该是唯一的）
 * [channel] plugin channel 的名称，用于发送数据。
 * [data] 任何数据，取决于频道。必须从数据包长度推断出此数组的长度。
 */
@Serializable
data class LoginPluginRequestPacket(
    val messageId: Int,
    val channel: String,
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
 * ### 登录插件请求解码
 *
 * @see LoginPluginRequestPacket
 */
class LoginPluginRequestDecoder : PacketDecoder<LoginPluginRequestPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): LoginPluginRequestPacket {
        val messageId = buf.readVarInt()
        val channel = buf.readString()
        val byteBuf = Unpooled.buffer(buf.readableBytes())
        buf.readBytes(byteBuf)
        return LoginPluginRequestPacket(messageId = messageId, channel = channel, data = byteBuf)
    }
}

/**
 * ### 登录插件请求编码
 *
 * @see LoginPluginRequestPacket
 */
class LoginPluginRequestEncoder : PacketEncoder<LoginPluginRequestPacket> {
    /**
     * 编码
     *
     * **服务器**
     */
    override fun encode(buf: ByteBuf, packet: LoginPluginRequestPacket): ByteBuf {
        buf.writeVarInt(packet.messageId)
        buf.writeString(packet.channel)
        buf.writeBytes(packet.data)
        return buf
    }
}
