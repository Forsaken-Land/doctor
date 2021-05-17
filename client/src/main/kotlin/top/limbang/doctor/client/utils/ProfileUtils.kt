package top.limbang.doctor.client.utils

import org.slf4j.LoggerFactory

/**
 *
 * @author WarmthDawn
 * @since 2021-05-17
 */
object ProfileUtils {
    val logger = LoggerFactory.getLogger(this.javaClass)

    fun exectueTime(msg: String, action: () -> Unit) {
        val time = System.currentTimeMillis()
        action()
        val elapsed = System.currentTimeMillis() - time
        println("$msg 执行时间: $elapsed ms")
    }
}