package top.limbang.doctor.core.plugin

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.api.registry.Registry
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.impl.plugin.DefaultHookProvider
import top.limbang.doctor.core.impl.registy.DefaultRegistry
import top.limbang.doctor.core.cast

/**
 * 插件管理器
 * @author WarmthDawn
 * @since 2021-05-14
 */
class PluginManager(
    private val emitter: EventEmitter,
    private val registry: DefaultRegistry<String, Plugin> = DefaultRegistry()
) : Registry<String, Plugin> by registry {
    private val hookProviderRegistry = HookProviderRegistry()
    private val pluginEventRegistry = DefaultRegistry<String, EventEmitter>()

    /**
     * 触发插件钩子
     */
    fun <T, V : DefaultHookProvider<T>> invokeHook(provider: Class<V>, args: T, clearHooks: Boolean = false) {
        val hooks = hookProviderRegistry.tryGet(provider)?.cast<V>()
        hooks?.invokeHook(args)
        if (clearHooks) {
            hooks?.clear()
            hookProviderRegistry.remove(provider)
        }
    }

    /**
     * 注册插件
     */
    override fun register(key: String, value: Plugin) {
        value.created()
        val redirect = DefaultEventEmitter()
        pluginEventRegistry.register(key, redirect)
        emitter.targetTo(redirect)
        value.registerEvent(redirect)
        value.hookProvider(hookProviderRegistry)
        registry.register(key, value)
    }

    /**
     * 移除插件
     */
    override fun remove(key: String) {
        if (have(key)) {
            val plugin = get(key)
            val redirect = pluginEventRegistry.get(key)
            emitter.removeTarget(redirect)
            plugin.destroy()
        }
        registry.remove(key)
        pluginEventRegistry.remove(key)
    }


}