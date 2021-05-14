package top.limbang.doctor.protocol.definition.status.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 * ### ping
 *
 * [payload] 可以是任何数字。Notchian客户端使用依赖于系统的时间值，以毫秒计。
 */
data class PingPacket(
    val payload: Long
) : Packet


/**
 * ### ping协议包编码
 *
 * @see PingPacket
 */
@Serializable
class PingEncoder : PacketEncoder<PingPacket> {

    /**
     * ### 编码 客户端
     * @param buf 缓冲区
     * @param packet ping协议包
     * @return [ByteBuf]
     */
    override fun encode(buf: ByteBuf, packet: PingPacket): ByteBuf {
        buf.writeLong(packet.payload)
        return buf
    }

}

/**
 * ### ping协议包解码
 *
 * @see PingPacket
 */
class PingDecoder : PacketDecoder<PingPacket> {

    /**
     * ### 解码 服务器
     * @param buf 缓冲区
     * @return [PingPacket]
     */
    override fun decoder(buf: ByteBuf): PingPacket {
        return PingPacket(payload = buf.readLong())
    }
}