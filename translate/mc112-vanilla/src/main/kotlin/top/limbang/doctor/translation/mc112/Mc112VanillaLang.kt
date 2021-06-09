package top.limbang.doctor.translation.mc112

import Mc112LangResources
import java.io.File
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
class Mc112VanillaLang : Mc112LangResources() {
    private val lock = ReentrantLock()
    override fun load() {
        lock.lock()
        if (!loaded) {
            val file = File(javaClass.getResource("/language/minecraft/lang/zh_cn.lang")!!.toURI())
            load(file)
            loaded = true
        }
        lock.unlock()
    }
}