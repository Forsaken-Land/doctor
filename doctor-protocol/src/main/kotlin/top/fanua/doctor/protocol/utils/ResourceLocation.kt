package top.fanua.doctor.protocol.utils

import kotlinx.serialization.Serializable

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/23:0:34
 */
@Serializable
data class ResourceLocation(
    val namespace: String,
    val path: String
) {
    constructor(resourceParts: Array<String>) : this(
        (resourceParts[0].ifEmpty { "minecraft" }), resourceParts[1]
    )

    constructor(resourceName: String) : this(
        decompose(resourceName, ':')
    )

    companion object {
        private fun decompose(resourceName: String, splitOn: Char): Array<String> {
            val astring = arrayOf("minecraft", resourceName)
            val i = resourceName.indexOf(splitOn)
            if (i >= 0) {
                astring[1] = resourceName.substring(i + 1, resourceName.length)
                if (i >= 1) {
                    astring[0] = resourceName.substring(0, i)
                }
            }
            return astring
        }
    }
}
