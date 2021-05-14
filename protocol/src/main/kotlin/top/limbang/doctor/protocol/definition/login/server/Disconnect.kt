package top.limbang.doctor.protocol.definition.login.server

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 * ### 登录时断开连接
 *
 * [reason] 断开连接的原因
 */
@Serializable
data class DisconnectPacket(
    val reason: String
) : Packet

/**
 * ### 断开解码
 *
 * @see DisconnectPacket
 */
class DisconnectDecoder : PacketDecoder<DisconnectPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): DisconnectPacket {
        return DisconnectPacket(reason = buf.readString())
    }
}

/**
 * ### 断开编码
 *
 * @see DisconnectPacket
 */
class DisconnectEncoder : PacketEncoder<DisconnectPacket> {
    /**
     * 编码
     *
     * **服务器**
     */
    override fun encode(buf: ByteBuf, packet: DisconnectPacket): ByteBuf {
        buf.writeString(packet.reason)
        return buf
    }
}