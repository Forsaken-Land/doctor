package top.limbang.doctor.client.session

import java.security.*
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-17
 */
typealias PropertyMap = MutableMap<String, MutableSet<Property>>
class Property(
    val name: String,
    val value: String,
    val signature: String?) {

    fun hasSignature(): Boolean {
        return signature != null
    }

    fun isSignatureValid(publicKey: PublicKey): Boolean {
        try {
            val signature = Signature.getInstance("SHA1withRSA")
            signature.initVerify(publicKey)
            signature.update(value.toByteArray())
            return signature.verify(Base64.getDecoder().decode(this.signature))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        }
        return false
    }
}