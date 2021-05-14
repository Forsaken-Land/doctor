package top.limbang.doctor.network.api.handler

import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.entity.ServiceResponse
import java.util.concurrent.Future
import javax.crypto.SecretKey

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/14:18:50
 */

interface Connection {
    val host: String
    val port: Int
    var protocolState: ProtocolState
    var modList: List<ServiceResponse.Mod>

    /**
     * 判断是否启用压缩
     */

    fun isCompressionEnabled(): Boolean

    /**
     * 设置压缩
     */
    fun setCompressionEnabled(threshold: Int)

    /**
     * 判断是否启用加密
     */
    fun isEncryptionEnabled(): Boolean

    /**
     * 设置加密
     */
    fun setEncryptionEnabled(sharedSecret: SecretKey)

    /**
     * 发送数据包
     */
    fun sendPacket(packet: Packet): Future<Void>

    fun close(packet: Packet?): Future<Void>

    /**
     * 关闭连接
     */
    fun close(): Future<Void>

    /**
     * 判断连接是否已关闭
     */
    fun isClosed(): Boolean
}