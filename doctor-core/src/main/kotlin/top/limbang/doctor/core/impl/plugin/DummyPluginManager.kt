package top.limbang.doctor.core.impl.plugin

import top.limbang.doctor.core.api.plugin.IHookMessage
import top.limbang.doctor.core.api.plugin.IPluginHookProvider
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.api.registry.RegistryException
import top.limbang.doctor.core.plugin.PluginHookRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class DummyPluginManager : IPluginManager {
    override fun onPluginEnabled() {

    }


    override fun <T : Plugin> registerPlugin(plugin: T) {
    }


    override fun <T : Plugin> removePlugin(key: Class<T>) {
    }

    override fun <T : Plugin> getPlugin(key: Class<T>): T {
        throw RegistryException("无法获取插件：当前插件管理器为空")
    }

    override fun getAllPlugins(): List<Plugin> {
        return emptyList()
    }

    override fun <T : Plugin> hasPlugin(key: Class<T>): Boolean {
        return true
    }

    override fun <T : IHookMessage> invokeHook(provider: IPluginHookProvider<T>, args: T, freezeHook: Boolean): Boolean {
        return false
    }

    override fun <T : IHookMessage> getHook(provider: IPluginHookProvider<T>): PluginHookRegistry<T> {
        throw RegistryException("无法获取钩子：当前插件管理器为空")
    }

    override fun <T : IHookMessage> removeHook(provider: IPluginHookProvider<T>) {

    }
}