package top.limbang.doctor.core.plugin

import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.IPluginHookManager
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.cast
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.impl.registy.DefaultRegistry

/**
 * 插件管理器
 * @author WarmthDawn
 * @since 2021-05-14
 */
class PluginManager(
    private val emitter: EventEmitter,
) : IPluginManager,
    IPluginHookManager by DefaultPluginHookManager() {
    private val pluginEventRegistry = DefaultRegistry<Class<*>, EventEmitter>()
    private val pluginRegistry = DefaultRegistry<Class<*>, Plugin>()
    private val enabledPlugins = mutableSetOf<Class<*>>()

    companion object {
        val log = LoggerFactory.getLogger(PluginManager::class.java)
    }

    /**
     * 注册插件
     */
    override fun <T : Plugin> registerPlugin(plugin: T) {
        val key = plugin.javaClass
        plugin.created(this)
        pluginRegistry.register(key, plugin)
    }

    override fun onPluginEnabled() {
        pluginRegistry.freeze(true)
        val allPlugins = pluginRegistry.all()
        while (pluginRegistry.size > enabledPlugins.size) {
            val plugin = allPlugins
                .firstOrNull { enabledPlugins.containsAll(it.dependencies) }

            if (plugin == null) {
                val notEnabled = allPlugins.filterNot { it.javaClass in enabledPlugins }
                notEnabled.forEach { plugin ->
                    val pluginName = plugin.javaClass.simpleName
                    val missingDependencies = plugin.dependencies.asSequence()
                        .filterNot { it.javaClass in enabledPlugins }
                        .joinToString { it.javaClass.simpleName }
                    log.warn("插件 $pluginName 缺少以下依赖 $missingDependencies")
                }
                break
            }
            val key = plugin.javaClass
            val redirect = if (plugin is EventEmitter) plugin else DefaultEventEmitter()
            pluginEventRegistry.register(key, redirect)
            emitter.targetTo(redirect)
            try {
                plugin.registerEvent(redirect)
                plugin.registerHook(this)
                enabledPlugins.add(plugin.javaClass)
            } catch (e: Exception) {
                allPlugins.remove(plugin)
                removePlugin(plugin.javaClass)
                log.error("启动插件失败", e)
            }
        }

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