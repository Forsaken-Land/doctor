package top.limbang.doctor.core.plugin

import top.limbang.doctor.core.api.plugin.IHookMessage
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.api.plugin.PluginHookHandler
import top.limbang.doctor.core.api.plugin.PluginHookHandlerImpl
import top.limbang.doctor.core.api.registry.RegistryException

/**
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
class PluginHookRegistry<T : IHookMessage> : Iterable<PluginHookHandler<T>> {
    private val set: MutableSet<PluginHookHandler<T>> = HashSet()
    var frozen: Boolean = false
        private set
    private var freezeReason = ""
    private fun checkFreeze() {
        if (frozen) {
            throw RegistryException(freezeReason)
        }
    }

    fun addHandler(data: PluginHookHandler<T>) {
        checkFreeze()
        set.add(data)
    }


    operator fun contains(data: PluginHookHandler<T>): Boolean {
        return set.contains(data)
    }

    fun all(): List<PluginHookHandler<T>> {
        return set.toList()
    }


    fun remove(data: PluginHookHandler<T>) {
        set.remove(data)
    }

    fun clear() {
        set.clear()
    }

    fun freeze(freeze: Boolean, reason: String) {
        this.frozen = freeze
        this.freezeReason = reason
    }

    fun freeze(reason: String) {
        freeze(true, reason)
    }


    override fun iterator(): Iterator<PluginHookHandler<T>> {
        return set.iterator()
    }
}

inline fun <reified T : IHookMessage> PluginHookRegistry<T>.addHandler(
    plugin: Plugin,
    priority: Int = 10,
    noinline handler: (T) -> Boolean
) {
    this.addHandler(PluginHookHandlerImpl(plugin, priority, handler, T::class.java))
}