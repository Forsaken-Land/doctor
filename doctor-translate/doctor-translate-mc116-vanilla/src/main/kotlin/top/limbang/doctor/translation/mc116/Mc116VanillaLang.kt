package top.limbang.doctor.translation.mc116

import Mc116LangResources
import java.io.File
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
class Mc116VanillaLang : Mc116LangResources() {
    private val lock = ReentrantLock()
    override fun load() {
        lock.lock()
        if (!loaded) {
            val file = File(javaClass.getResource("/mc116vanillalang/zh_cn.json")!!.toURI())
            load(file)
            loaded = true
        }
        lock.unlock()
    }
}