package top.fanua.doctor.translation.api

import top.fanua.doctor.translation.core.DummyI18n
import top.fanua.doctor.translation.core.MultiResourceI18n
import top.fanua.doctor.translation.core.SingleResourceI18n
import java.util.*
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
interface I18n {
    fun translate(key: String): String
    fun translateItem(start: String): Map<String, String>
    fun translate(key: String, vararg parameter: Any): String {
        val s = translate(key)
        return try {
            String.format(s, *parameter)
        } catch (e: IllegalFormatException) {
            "Format error: $s"
        }
    }

    fun setDefault() {
        _default = this
    }

    companion object {
        private var _default: I18n? = null
        private val lock = ReentrantLock()
        private fun findClass(name: String): Class<*>? {
            return try {
                Class.forName(name)
            } catch (e: ClassNotFoundException) {
                null
            }
        }

        val DEFAULT: I18n
            get() {
                lock.lock()
                try {
                    if (_default == null) {
                        val resources = ArrayList<Class<*>>()
                        findClass("top.fanua.doctor.translation.mc112.Mc112ModLang")
                            ?.let { resources.add(it) }
                        findClass("top.fanua.doctor.translation.mc112.Mc112VanillaLang")
                            ?.let { resources.add(it) }
                        findClass("top.fanua.doctor.translation.mc116.Mc116VanillaLang")
                            ?.let { resources.add(it) }

                        when (resources.size) {
                            0 -> DummyI18n
                            1 -> SingleResourceI18n.create(resources[0])
                            else -> MultiResourceI18n().also {
                                resources.forEach(it::add)
                            }
                        }.setDefault()

                    }
                    return _default!!
                } finally {
                    lock.unlock()
                }
            }
    }
}
