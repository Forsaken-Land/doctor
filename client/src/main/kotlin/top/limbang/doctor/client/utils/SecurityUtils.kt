package top.limbang.doctor.client.utils

import java.math.BigInteger
import java.security.Key
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object SecurityUtils {

    /**
     * ### 解码公匙
     * @param publicKey 要解码的公匙
     */
    fun decodePublicKey(publicKey: ByteArray): PublicKey {
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicKey))
    }

    /**
     * ### 生成共享密匙
     */
    fun generateSharedKey(): SecretKey {
        val generator = KeyGenerator.getInstance("AES")
        generator.init(128)
        return generator.generateKey()
    }

    /**
     * ### 加密 RSA 算法
     */
    fun encryptRSA(key: Key, data: ByteArray): ByteArray {
        val rsaCipher = Cipher.getInstance("RSA")
        rsaCipher.init(Cipher.ENCRYPT_MODE, key)
        return rsaCipher.doFinal(data)
    }

    /**
     * ### 生成身份认证哈希
     * @param serverId
     * @param sharedSecret
     * @param publicKey
     */
    fun generateAuthHash(serverId: String, sharedSecret: SecretKey, publicKey: PublicKey): String {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update(serverId.toByteArray(Charsets.US_ASCII))
        digest.update(sharedSecret.encoded)
        digest.update(publicKey.encoded)
        return BigInteger(digest.digest()).toString(16)
    }
}