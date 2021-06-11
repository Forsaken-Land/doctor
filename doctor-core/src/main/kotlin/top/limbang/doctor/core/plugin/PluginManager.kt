package top.limbang.doctor.core.plugin

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.cast
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.impl.plugin.DefaultHookProvider
import top.limbang.doctor.core.impl.registy.DefaultRegistry

/**
 * 插件管理器
 * @author WarmthDawn
 * @since 2021-05-14
 */
class PluginManager(
    private val emitter: EventEmitter,
) : IPluginManager {
    private val hookProviderRegistry = HookProviderRegistry()
    private val pluginEventRegistry = DefaultRegistry<Class<*>, EventEmitter>()
    private val pluginRegistry = DefaultRegistry<Class<*>, Plugin>()

    override fun <T, V : DefaultHookProvider<T>> invokeHook(provider: Class<V>, args: T, clearHooks: Boolean) {
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
    override fun <T : Plugin> registerPlugin(plugin: T) {
        val key = plugin.javaClass
        plugin.created(this)
        val redirect = if (plugin is EventEmitter)
            plugin as EventEmitter
        else DefaultEventEmitter()


        pluginEventRegistry.register(key, redirect)
        emitter.targetTo(redirect)
        plugin.registerEvent(redirect)
        plugin.hookProvider(hookProviderRegistry)
        pluginRegistry.register(key, plugin)
    }

    /**
     * 移除插件
     */
    override fun <T : Plugin> removePlugin(key: Class<T>) {
        if (pluginRegistry.have(key)) {
            val plugin = pluginRegistry.get(key)
            val redirect = pluginEventRegistry.get(key)
            emitter.removeTarget(redirect)
            plugin.destroy()
        }
        pluginRegistry.remove(key)
        pluginEventRegistry.remove(key)
    }

    override fun <T : Plugin> getPlugin(key: Class<T>) = pluginRegistry.get(key).cast<T>()
    override fun getAllPlugins() = pluginRegistry.all()
    override fun <T : Plugin> hasPlugin(key: Class<T>) = pluginRegistry.have(key)


}