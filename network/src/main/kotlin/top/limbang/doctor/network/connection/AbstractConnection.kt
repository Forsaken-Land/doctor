package top.limbang.doctor.network.connection

import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.entity.ServiceResponse
import javax.crypto.SecretKey

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/14:20:21
 */
abstract class AbstractConnection(
    override val host: String,
    override val port: Int,
    override var protocolState: ProtocolState,
    override var modList: List<ServiceResponse.Mod> = listOf()
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