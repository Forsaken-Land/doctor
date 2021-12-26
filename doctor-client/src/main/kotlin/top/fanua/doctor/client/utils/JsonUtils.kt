package top.fanua.doctor.client.utils

import kotlinx.serialization.json.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-28
 */

val JsonElement.node: JsonNode get() = JsonNode(this)
val JsonNode.int get() = jsonElement.jsonPrimitive.int
val JsonNode.double get() = jsonElement.jsonPrimitive.double
val JsonNode.float get() = jsonElement.jsonPrimitive.float
val JsonNode.long get() = jsonElement.jsonPrimitive.long
val JsonNode.boolean get() = jsonElement.jsonPrimitive.boolean
val JsonNode.jsonArray get() = jsonElement.jsonArray
val JsonNode.jsonObject get() = jsonElement.jsonObject
val JsonNode.content get() = jsonElement.jsonPrimitive.content

class JsonNode(
    val jsonElement: JsonElement
) {
    operator fun get(key: String): JsonNode = JsonNode(jsonElement.jsonObject[key]!!)
    operator fun get(index: Int) = JsonNode(jsonElement.jsonArray[index])
    operator fun contains(key: String) = jsonElement is JsonObject && jsonElement.containsKey(key)
    val size get() = if (jsonElement is JsonArray) jsonElement.size else 0
    val type: JsonType
        get() {
            return when (jsonElement) {
                is JsonArray -> JsonType.JsonArray
                is JsonObject -> JsonType.JsonObject
                is JsonNull -> JsonType.Null
                is JsonPrimitive -> when {
                    jsonElement.isString -> JsonType.String
                    jsonElement.booleanOrNull != null -> JsonType.Boolean
                    else -> JsonType.Number
                }
                else -> JsonType.Unknown
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsonNode) return false

        if (jsonElement != other.jsonElement) return false

        return true
    }

    override fun hashCode(): Int = jsonElement.hashCode()

}

enum class JsonType {
    JsonArray,
    JsonObject,
    String,
    Number,
    Boolean,
    Null,
    Unknown
}
