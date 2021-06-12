import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import top.limbang.doctor.translation.api.IResources
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
abstract class Mc116LangResources : IResources {
    private val properties: MutableMap<String, String> = HashMap()
    override var loaded: Boolean = false
        protected set

    fun load(vararg files: File) {
        properties.clear()
        files.filter {
            it.exists()
        }.forEach {
            loadLangFile(it)
        }
    }

    private fun loadLangFile(file: File) {
        val obj = Json.parseToJsonElement(file.readText(StandardCharsets.UTF_8)) as JsonObject
        obj.entries.forEach {
            val (key, v) = it
            properties[key] = PATTERN.matcher(v.jsonPrimitive.content).replaceAll("%$1s")
        }

    }

    override fun contains(key: String): Boolean {
        return properties.containsKey(key)
    }

    override fun get(key: String): String = properties[key] ?: key

    companion object {
        /** Pattern that matches numeric variable placeholders in a resource string, such as "%d", "%3$d", "%.2f"  */
        private val PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]")

    }
}