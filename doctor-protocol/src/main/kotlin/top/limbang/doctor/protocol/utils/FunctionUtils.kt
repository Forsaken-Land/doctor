package top.limbang.doctor.protocol.utils

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

/**
 * 柯里化
 */
fun <T1, T2, TR> ((T1, T2) -> TR).currying(): (T1) -> (T2) -> TR {
    return fun(arg1: T1): (T2) -> TR {
        return fun(arg2: T2): TR {
            return this(arg1, arg2)
        }
    }
}


fun <K, V> MutableMap<K, V>.getOrCreate(key: K, default: () -> V): V {
    if (!this.containsKey(key)) {
        this[key] = default()
    }
    return this[key]!!
}
