package top.limbang.doctor.network.api

import io.netty.util.concurrent.Future
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.protocol.api.Packet
import javax.crypto.SecretKey

/**
 * ### 连接接口
 *
 * 连接不应该实现此类,应该继承[AbstractConnection]
 */
interface Connection {
    val host: String
    val port: Int
    val emitter: EventEmitter

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
    fun sendPacket(packet: Packet): Future<*>

    fun close(packet: Packet?): Future<*>

    /**
     * 关闭连接
     */
    fun close(): Future<*>

    /**
     * 判断连接是否已关闭
     */
    fun isClosed(): Boolean
}