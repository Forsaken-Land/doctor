package top.fanua.doctor.protocol.definition.login.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.readUUID
import top.fanua.doctor.protocol.extension.writeString
import java.util.*

/**
 * ### 登录成功
 *
 * [uUID] 与其他数据包不同，此字段将UUID作为带连字符的字符串包含。
 * [userName] 玩家的用户名
 */
@Serializable
data class LoginSuccessPacket(
    @Contextual
    val uUID: UUID,
    val userName: String
) : Packet

/**
 * ### 登录成功解码
 *
 * @see LoginSuccessPacket
 */
class LoginSuccess340Decoder : PacketDecoder<LoginSuccessPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): LoginSuccessPacket {
        val uUID = UUID.fromString(buf.readString())
        val userName = buf.readString()
        return LoginSuccessPacket(uUID = uUID, userName = userName)
    }
}

class LoginSuccessAfter340Decoder : PacketDecoder<LoginSuccessPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): LoginSuccessPacket {
        val uUID = buf.readUUID()
        val userName = buf.readString()
        return LoginSuccessPacket(uUID = uUID, userName = userName)
    }
}

/**
 * ### 登录成功编码
 *
 * @see LoginSuccessPacket
 */
class LoginSuccessEncoder : PacketEncoder<LoginSuccessPacket> {
    /**
     * 编码
     *
     * **服务器**
     */
    override fun encode(buf: ByteBuf, packet: LoginSuccessPacket): ByteBuf {
        buf.writeString(packet.uUID.toString())
        buf.writeString(packet.userName)
        return buf
    }
}
