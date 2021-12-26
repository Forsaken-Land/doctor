package top.fanua.doctor.client.session

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-17
 */
@Serializable
class GameProfile(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID?,
    val name: String
) {

    fun isComplete(): Boolean {
        return id != null
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(fromUUID(value))
    }


    fun fromString(input: String): UUID {
        return UUID.fromString(
            input.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                "$1-$2-$3-$4-$5"
            )
        )
    }

    fun fromUUID(value: UUID): String {
        return value.toString().replace("-", "")
    }

}

fun GameProfile.getOfflineProfile(): GameProfile {
    val uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.name).toByteArray(StandardCharsets.UTF_8))
    return GameProfile(uuid, this.name)
}
