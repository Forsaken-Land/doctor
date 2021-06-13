package top.limbang.doctor.core.api.plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
interface IPluginManager: IPluginHookManager {
    fun onPluginEnabled()
    /**
     * 注册插件
     */
    fun <T: Plugin> registerPlugin(plugin: T)

    /**
     * 移除插件
     */
    fun <T: Plugin> removePlugin(key: Class<T>)

    fun <T: Plugin> getPlugin(key: Class<T>): T
    fun getAllPlugins(): List<Plugin>
    fun <T: Plugin> hasPlugin(key: Class<T>): Boolean

}