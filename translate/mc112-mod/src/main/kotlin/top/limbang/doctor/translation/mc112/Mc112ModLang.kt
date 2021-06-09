package top.limbang.doctor.translation.mc112

import Mc112LangResources
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantLock

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
class Mc112ModLang : Mc112LangResources() {
    private val lock = ReentrantLock()
    override fun load() {
        lock.lock()
        if (!loaded) {
            val rootPath = File(javaClass.getResource("/mc112modlang/")!!.toURI())
            val files = (rootPath.list() ?: emptyArray()).map {
                Paths.get(rootPath.path, it, "lang", "zh_cn.lang").toFile()
            }
            load(*files.toTypedArray())
            loaded = true
        }
        lock.unlock()
    }
}