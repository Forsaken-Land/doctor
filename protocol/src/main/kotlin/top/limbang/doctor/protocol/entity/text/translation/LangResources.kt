package top.limbang.doctor.protocol.entity.text.translation

import com.google.common.collect.Maps
import com.google.common.io.Resources
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
class LangResources {
    private val properties: MutableMap<String, String> = Maps.newHashMap()
    private var unicode = false

    fun isUnicode(): Boolean {
        return unicode
    }

    private fun checkUnicode() {
        unicode = false
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
        unicode = f.toDouble() > 0.1
    }

    fun loadLocaleData() {
        properties.clear()
        val rootPath = File(Resources.getResource("language").toURI())
        (rootPath.list() ?: emptyArray()).map {
            Paths.get(rootPath.path, it, "lang", "zh_cn.lang").toFile()
        }.filter {
            it.exists()
        }.forEach {
            loadLocaleData(it)
        }
        checkUnicode()
    }

    private fun loadLocaleData(file: File) {
        file.forEachLine(StandardCharsets.UTF_8) { str ->
            str.takeIf {
                it.isNotEmpty() && it[0] != '#'
            }?.split('=', limit = 2)?.takeIf { it.size == 2 }?.let {
                val (key, v) = it
                properties[key] = PATTERN.matcher(v).replaceAll("%$1s")
            }
        }
    }

    fun rawMessage(translateKey: String) = properties[translateKey] ?: translateKey

    fun formatMessage(translateKey: String, parameters: Array<Any?>): String {
        val s = rawMessage(translateKey)
        return try {
            String.format(s, *parameters)
        } catch (var5: IllegalFormatException) {
            "Format error: $s"
        }
    }

    fun hasKey(key: String): Boolean {
        return properties.containsKey(key)
    }

    companion object {
        private val PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]")
    }
}