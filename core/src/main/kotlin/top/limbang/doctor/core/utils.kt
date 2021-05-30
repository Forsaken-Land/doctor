package top.limbang.doctor.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType

/**
 *
 * @author WarmthDawn
 * @since 2021-05-13
 */

/**
 * 某个讨厌泛型警告的强制转换
 */
fun <T : Any> Any.cast(): T {
    return this as T
}

fun <T> tryGetGenericType(cls: Class<*>): Class<T>? {
    val type = cls.genericInterfaces.find { it is ParameterizedType }
    if (type != null) {
        try {
            (type as ParameterizedType).actualTypeArguments.forEach {
                if (it is Class<*>) {
                    return it.cast()
                }
            }
        } catch (e: Exception) {
        }
    }
    return null
}

fun setTimeout(time: Long, action: () -> Unit): Job {
    return GlobalScope.launch {
        delay(time)
        action()
    }
}
