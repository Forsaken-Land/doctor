package top.limbang.doctor.core.impl.registy

import top.limbang.doctor.core.api.plugin.PluginHookHandler
import top.limbang.doctor.core.api.registry.Registry
import top.limbang.doctor.core.api.registry.RegistryException
import top.limbang.doctor.core.plugin.PluginHookRegistry

/**
 * 注册
 * @author WarmthDawn
 * @since 2021-05-14
 */
class DefaultRegistry<K, V> : Registry<K, V> {
    private val map: MutableMap<K, V> = HashMap()
    private var freeze: Boolean = false
    private var freezeReason = ""
    private fun checkFreeze() {
        if (freeze) {
            throw RegistryException(freezeReason)
        }
    }

    override fun register(key: K, value: V) {
        checkFreeze()
        map[key] = value
    }

    override fun have(key: K): Boolean {
        return map.containsKey(key)
    }

    override fun tryGet(key: K): V? {
        return map[key]
    }

    override fun all(): Iterable<V> {
        return map.values
    }

    override fun remove(key: K) {
        map.remove(key)
    }

    override val size: Int get() {
        return map.size
    }

    override fun freeze(freeze: Boolean, reason: String) {
        this.freeze = freeze
        this.freezeReason = reason
    }


}