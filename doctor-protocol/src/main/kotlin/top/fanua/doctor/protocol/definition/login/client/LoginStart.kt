package top.fanua.doctor.protocol.definition.login.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.writeString

/**
 * ### 开始登录
 *
 * - [name] 玩家的用户名
 */
@Serializable
data class LoginStartPacket(
    val name: String
) : Packet

/**
 * ### 登录开始编码
 *
 * @see LoginStartPacket
 */
class LoginStartEncoder : PacketEncoder<LoginStartPacket> {
    /**
     * 编码
     *
     * **客户端**
     */
    override fun encode(buf: ByteBuf, packet: LoginStartPacket): ByteBuf {
        buf.writeString(packet.name)
        return buf
    }
}

/**
 * ### 登录开始解码
 *
 * @see LoginStartPacket
 */
class LoginStartDecoder : PacketDecoder<LoginStartPacket> {
    /**
     * 解码
     *
     * **服务器**
     */
    override fun decoder(buf: ByteBuf): LoginStartPacket {
        return LoginStartPacket(name = buf.readString())
    }
}
