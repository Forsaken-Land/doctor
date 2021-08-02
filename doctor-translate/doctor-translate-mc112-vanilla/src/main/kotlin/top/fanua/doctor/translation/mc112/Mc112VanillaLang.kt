package top.fanua.doctor.translation.mc112

import Mc112LangResources
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
            val file = javaClass.getResource("/mc112vanillalang/zh_cn.lang")?.openStream()
            load(file)
            loaded = true
        }
        lock.unlock()
    }
}
