package top.limbang.doctor.core.api.plugin

import top.limbang.doctor.core.impl.plugin.DefaultHookProvider

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
interface IPluginManager {
    /**
     * 触发插件钩子
     */
    fun <T, V : DefaultHookProvider<T>> invokeHook(provider: Class<V>, args: T, clearHooks: Boolean = false)

    /**
     * 注册插件
     */
    fun <T: Plugin> registerPlugin(key: Class<T>, value: Plugin)

    /**
     * 移除插件
     */
    fun <T: Plugin> removePlugin(key: Class<T>)

    fun <T: Plugin> getPlugin(key: Class<T>): Plugin
    fun getAllPlugins(): List<Plugin>
    fun <T: Plugin> hasPlugin(key: Class<T>): Boolean

}