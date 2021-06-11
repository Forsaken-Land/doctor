package top.limbang.doctor.protocol.definition.login.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.*

/**
 * ### 加密响应
 *
 * - [sharedSecret] 客户端生成的共享密匙
 * - [verifyToken] 服务器加密请求包 [EncryptionRequestPacket.verifyToken] 字段用 [EncryptionRequestPacket.publicKey] 加密后的数据
 */
@Serializable
data class EncryptionResponsePacket(
    val sharedSecret: ByteArray,
    val verifyToken: ByteArray
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncryptionResponsePacket) return false

        if (!sharedSecret.contentEquals(other.sharedSecret)) return false
        if (!verifyToken.contentEquals(other.verifyToken)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sharedSecret.contentHashCode()
        result = 31 * result + verifyToken.contentHashCode()
        return result
    }
}

/**
 * ### 加密响应编码
 *
 * @see EncryptionResponsePacket
 */
class EncryptionResponseEncoder : PacketEncoder<EncryptionResponsePacket> {
    /**
     * 编码
     *
     * **客户端**
     */
    override fun encode(buf: ByteBuf, packet: EncryptionResponsePacket): ByteBuf {
        buf.writeByteArray(packet.sharedSecret.size, packet.sharedSecret)
        buf.writeByteArray(packet.verifyToken.size, packet.verifyToken)
        return buf
    }
}

/**
 * ### 加密响应解码
 *
 * @see EncryptionResponsePacket
 */
class EncryptionResponseDecoder : PacketDecoder<EncryptionResponsePacket> {
    /**
     * 解码
     *
     * **服务器**
     */
    override fun decoder(buf: ByteBuf): EncryptionResponsePacket {
        val sharedSecret = buf.readByteArray()
        val verifyToken = buf.readByteArray()
        return EncryptionResponsePacket(
            sharedSecret = sharedSecret,
            verifyToken = verifyToken
        )
    }
}