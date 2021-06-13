package top.limbang.doctor.core.api.registry

import top.limbang.doctor.core.api.plugin.PluginHookHandler
import top.limbang.doctor.core.plugin.PluginHookRegistry

/**
 * 注册系统
 *  [K] 注册的 Key 类型
 *  [V] 注册的 Value 类型
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
    fun all(): Iterable<V>

    /**
     * 禁止添加新的注册
     */
    fun freeze(freeze: Boolean) = freeze(freeze, "注册被锁定")
    fun freeze(freeze: Boolean, reason: String)
    val size: Int
}