package top.limbang.doctor.core.impl.plugin

import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.api.registry.RegistryException

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class DummyPluginManager : IPluginManager {
    override fun <T, V : DefaultHookProvider<T>> invokeHook(provider: Class<V>, args: T, clearHooks: Boolean) {

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
}