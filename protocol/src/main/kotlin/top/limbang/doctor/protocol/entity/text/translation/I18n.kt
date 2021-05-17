package top.limbang.doctor.protocol.entity.text.translation

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
object I18n {
    private val lock = ReentrantLock()
    fun translate(key: String): String {
        lock.lock()
        if(!Resources.loaded){
            Resources.loadLocaleData()
        }
        lock.unlock()
        return Resources.rawMessage(key)
    }

    val Resources: LangResources = LangResources()
}