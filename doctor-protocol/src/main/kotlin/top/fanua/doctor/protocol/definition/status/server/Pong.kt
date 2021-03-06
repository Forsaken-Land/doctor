package top.fanua.doctor.protocol.definition.status.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder


/**
 * ### Pong
 *
 * [payload] 应该与客户端发送的相同
 */
@Serializable
data class PongPacket(
    val payload: Long
) : Packet

/**
 * ### Pong协议包解码
 * @see PongPacket
 */
class PongDecoder : PacketDecoder<PongPacket> {
    /**
     * ### 解码 客户端
     */
    override fun decoder(buf: ByteBuf): PongPacket {
        val payload = buf.readLong()
        return PongPacket(payload = payload)
    }

}

/**
 * ### Pong协议包编码
 * @see PongPacket
 */
class PongEncoder : PacketEncoder<PongPacket> {

    /**
     * ### 编码 服务器
     */
    override fun encode(buf: ByteBuf, packet: PongPacket): ByteBuf {
        buf.writeLong(packet.payload)
        return buf
    }
}
