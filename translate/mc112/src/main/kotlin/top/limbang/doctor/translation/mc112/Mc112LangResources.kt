import top.limbang.doctor.translation.api.IResources
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
abstract class Mc112LangResources: IResources {
    private val properties: MutableMap<String, String> = HashMap()
    override var loaded: Boolean = false
        protected set
    override var isUnicode = false
        protected set

    private fun checkUnicode() {
        isUnicode = false
        var i = 0
        var j = 0
        for (s in properties.values) {
            val k = s.length
            j += k
            for (l in 0 until k) {
                if (s[l].toInt() >= 256) {
                    ++i
                }
            }
        }
        val f = i.toFloat() / j.toFloat()
        isUnicode = f.toDouble() > 0.1
    }

    fun load(vararg files: File) {
        properties.clear()
        files.filter {
            it.exists()
        }.forEach {
            loadLangFile(it)
        }
        checkUnicode()
    }

    private fun loadLangFile(file: File) {
        file.forEachLine(StandardCharsets.UTF_8) { str ->
            str.takeIf {
                it.isNotEmpty() && it[0] != '#'
            }?.split('=', limit = 2)?.takeIf { it.size == 2 }?.let {
                val (key, v) = it
                properties[key] = PATTERN.matcher(v).replaceAll("%$1s")
            }
        }
    }

    override fun contains(key: String): Boolean {
        return properties.containsKey(key)
    }

    override fun get(key: String): String = properties[key] ?: key

    companion object {
        private val PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]")
    }
}