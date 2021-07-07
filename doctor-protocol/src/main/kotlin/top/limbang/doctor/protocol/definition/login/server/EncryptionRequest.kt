package top.limbang.doctor.protocol.definition.login.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.readByteArray
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.writeByteArray
import top.limbang.doctor.protocol.extension.writeString
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec

/**
 * ### 加密请求
 *
 * - [serverID] 服务器id,一般为空
 * - [publicKey] 服务器公钥,用数据 [X509EncodedKeySpec]创建X509EncodedKey,并使用 RSA [KeyFactory.generatePublic] 生成公钥
 * - [verifyToken] 服务器生成的随机字节序列
 */
@Serializable
data class EncryptionRequestPacket(
    val serverID: String,
    val publicKey: ByteArray,
    val verifyToken: ByteArray
) : Packet {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EncryptionRequestPacket) return false

        if (serverID != other.serverID) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (!verifyToken.contentEquals(other.verifyToken)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverID.hashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + verifyToken.contentHashCode()
        return result
    }
}

/**
 * ### 加密请求编解码
 *
 * @see EncryptionRequestPacket
 */
class EncryptionRequestDecoder : PacketDecoder<EncryptionRequestPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): EncryptionRequestPacket {
        val serverID = buf.readString()
        val publicKeyArray = buf.readByteArray()
        val verifyTokenArray = buf.readByteArray()
        return EncryptionRequestPacket(
            serverID = serverID,
            publicKey = publicKeyArray,
            verifyToken = verifyTokenArray
        )
    }
}

class EncryptionRequestBeforeDecoder : PacketDecoder<EncryptionRequestPacket> {
    /**
     * 解码
     *
     * **客户端**
     */
    override fun decoder(buf: ByteBuf): EncryptionRequestPacket {
        val serverID = buf.readString()
        val publicKeyArray = ByteArray(buf.readShort().toInt()).also {
            buf.readBytes(it)
        }
        val verifyTokenArray = ByteArray(buf.readShort().toInt()).also {
            buf.readBytes(it)
        }
        return EncryptionRequestPacket(
            serverID = serverID,
            publicKey = publicKeyArray,
            verifyToken = verifyTokenArray
        )
    }
}

/**
 * ### 加密请求编码
 *
 * @see EncryptionRequestPacket
 */
class EncryptionRequestEncoder : PacketEncoder<EncryptionRequestPacket> {
    /**
     * 编码
     *
     * **服务器**
     */
    override fun encode(buf: ByteBuf, packet: EncryptionRequestPacket): ByteBuf {
        buf.writeString(packet.serverID)
        buf.writeByteArray(packet.publicKey.size, packet.publicKey)
        buf.writeByteArray(packet.verifyToken.size, packet.verifyToken)
        return buf
    }
}
