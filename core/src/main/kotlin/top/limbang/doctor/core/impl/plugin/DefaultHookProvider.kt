package top.limbang.doctor.core.impl.plugin

import top.limbang.doctor.core.api.plugin.Hook
import top.limbang.doctor.core.api.plugin.PluginHookProvider

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
open class DefaultHookProvider<T> : PluginHookProvider<T> {
    private val hooks: MutableSet<Hook<T>> = HashSet()
    override fun addHook(hook: Hook<T>) {
        hooks.add(hook)
    }

    override fun delHook(hook: Hook<T>) {
        hooks.remove(hook)
    }

    fun clear() {
        hooks.clear()
    }

    fun invokeHook(arg: T) {
        hooks.forEach {
            it(arg)
        }
    }


}