package top.limbang.doctor.network.api

import javax.crypto.SecretKey

/**
 * ### 连接抽象类
 *
 * @see Connection
 */
abstract class AbstractConnection(
    override val host: String,
    override val port: Int,
) : Connection {
    private var encryptionEnabled: Boolean = false
    private var compressionEnabled: Boolean = false

    override fun isCompressionEnabled(): Boolean {
        return compressionEnabled
    }

    override fun setCompressionEnabled(threshold: Int) {
        this.compressionEnabled = threshold > 0
    }

    override fun setEncryptionEnabled(sharedSecret: SecretKey) {
        this.encryptionEnabled = sharedSecret.encoded.isNotEmpty()
    }

    override fun isEncryptionEnabled(): Boolean {
        return encryptionEnabled
    }
}