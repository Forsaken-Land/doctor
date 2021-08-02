package top.fanua.doctor.protocol.utils

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class JsonUtils

fun JsonObject.stringChild(name: String): String {
    return this[name]!!.jsonPrimitive.content
}

fun JsonObject.stringChildNullable(name: String): String? {
    return if (this.contains(name)) {
        this[name]?.jsonPrimitive?.contentOrNull
    } else {
        null
    }
}
