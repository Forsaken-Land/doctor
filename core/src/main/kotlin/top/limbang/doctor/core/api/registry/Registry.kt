package top.limbang.doctor.core.api.registry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface Registry<K, V> {
    fun register(key: K, value: V)
    fun have(key: K): Boolean
    fun get(key: K): V {
        return tryGet(key) ?: throw RegistryException("$key 未注册")
    }

    fun tryGet(key: K): V?
    fun remove(key: K)
    fun all(): List<V>

    /**
     * 禁止添加新的注册
     */
    fun freeze(freeze: Boolean) = freeze(freeze, "注册被锁定")
    fun freeze(freeze: Boolean, reason: String)
}