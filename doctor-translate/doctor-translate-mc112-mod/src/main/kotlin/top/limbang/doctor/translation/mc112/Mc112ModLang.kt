package top.limbang.doctor.translation.mc112

import Mc112LangResources
import java.io.File
import java.nio.charset.StandardCharsets
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
            val resourcesList = javaClass
                .getResource("/mc112modlang/translateList")
                ?.readText(StandardCharsets.UTF_8)
                ?.split("\n")
            val files = (resourcesList ?: emptyList()).map {
                javaClass.getResource("/mc112modlang$it")?.openStream()
            }
            load(*files.toTypedArray())
            loaded = true
        }
        lock.unlock()
    }
}