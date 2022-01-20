import top.fanua.doctor.translation.api.IResources
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
abstract class Mc112LangResources : IResources {
    private val properties: MutableMap<String, String> = HashMap()
    override var loaded: Boolean = false
        protected set
    var isUnicode = false
        protected set

    private fun checkUnicode() {
        isUnicode = false
        var i = 0
        var j = 0
        for (s in properties.values) {
            val k = s.length
            j += k
            for (l in 0 until k) {
                if (s[l].code >= 256) {
                    ++i
                }
            }
        }
        val f = i.toFloat() / j.toFloat()
        isUnicode = f.toDouble() > 0.1
    }

    fun load(vararg files: InputStream?) {
        properties.clear()
        files
            .filterNotNull()
            .forEach { loadLangFile(it) }
        checkUnicode()
    }

    private fun loadLangFile(file: InputStream) {
        BufferedReader(InputStreamReader(file, StandardCharsets.UTF_8))
            .forEachLine { str ->
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

    override fun startWith(start: String): Boolean {
        return properties.filter { it.key.startsWith(start) }.isNotEmpty()
    }

    override fun getList(start: String): Map<String, String> {
        val list = properties.filter { it.key.startsWith(start) }
        return list.ifEmpty { mapOf(Pair(start, start)) }
    }

    override fun get(key: String): String = properties[key] ?: key

    companion object {
        private val PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]")
    }
}
