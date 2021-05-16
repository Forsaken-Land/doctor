package top.limbang.doctor.protocol.definition.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.api.ProtocolState

/**
 * ### 握手协议包
 *
 * - [version] **VarInt** 参见 协议版本号 (在 Minecraft 1.12.2中值为 430)
 * - [address] **String** 主机名或 IP ，例如 localhost 或 127.0.0.1
 * - [port]    **Short** 默认值为 25565 ，官方服务端不使用该信息。
 * - [state]   **VarInt** 1:表示状态;2:表示登录
 */
@Serializable
data class HandshakePacket(
    val version: Int,
    val address: String,
    val port: Int,
    val state: ProtocolState
) : Packet

/**
 * ### 握手协议包编码
 *
 * @see HandshakePacket
 */
class HandshakeEncoder : PacketEncoder<HandshakePacket> {
    /**
     * 编码
     *
     * **客户端**
     */
    override fun encode(buf: ByteBuf, packet: HandshakePacket): ByteBuf {
        buf.writeVarInt(packet.version)
        buf.writeString(packet.address)
        buf.writeShort(packet.port)
        buf.writeVarInt(packet.state.id)
        return buf
    }

}

/**
 * ### 握手协议包解码
 *
 * @see HandshakePacket
 */
class HandshakeDecoder : PacketDecoder<HandshakePacket> {
    /**
     * 解码
     *
     * **服务器**
     */
    override fun decoder(buf: ByteBuf): HandshakePacket {
        val version = buf.readVarInt()
        val address = buf.readString()
        val port = buf.readUnsignedShort()
        val state = buf.readVarInt()
        return HandshakePacket(
            version = version,
            address = address,
            port = port,
            state = ProtocolState.fromId(state)
        )
    }
}

