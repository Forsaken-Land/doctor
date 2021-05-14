package top.limbang.doctor.protocol.definition.login.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

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
    val data: ByteArray
) : Packet


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
        val data = ByteArray(buf.readableBytes())
        buf.readBytes(data)
        return LoginPluginResponsePacket(messageId, successful, data)
    }
}