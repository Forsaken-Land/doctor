package top.limbang.doctor.protocol.utils

import com.google.gson.JsonObject

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
class JsonUtils {

}

fun JsonObject.stringChild(name: String): String {
    return this.getAsJsonPrimitive(name).asString
}

fun JsonObject.stringChildNullable(name: String): String? {
    return if (this.has(name)) {
        this.getAsJsonPrimitive(name).asString
    } else {
        null
    }
}