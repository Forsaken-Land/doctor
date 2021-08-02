package top.fanua.doctor.core.plugin

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.plugin.*
import top.fanua.doctor.core.cast
import top.fanua.doctor.core.impl.event.DefaultEventEmitter
import top.fanua.doctor.core.impl.registy.DefaultRegistry

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
        val log: Logger = LoggerFactory.getLogger(PluginManager::class.java)
    }

    /**
     * 注册插件
     */
    override fun <T : Plugin> registerPlugin(plugin: T) {
        val key = plugin.javaClass
        emitter.emit(PluginEvent.BeforeCreate, PluginEventArgs(this, plugin))
        plugin.created(this)
        emitter.emit(PluginEvent.Created, PluginEventArgs(this, plugin))
        pluginRegistry.register(key, plugin)
    }

    override fun onPluginEnabled() {
        pluginRegistry.freeze(true, "插件注册已经被锁定，请检查您插件注册的时机")
        val allPlugins = pluginRegistry.all()
        while (pluginRegistry.size > enabledPlugins.size) {
            val plugin = allPlugins
                .firstOrNull {
                    (!enabledPlugins.contains(it::class.java))
                            && enabledPlugins.containsAll(it.dependencies)
                }

            if (plugin == null) {
                val notEnabled = allPlugins.filterNot { it.javaClass in enabledPlugins }
                notEnabled.forEach { enabledPlugin ->
                    val pluginName = enabledPlugin.javaClass.simpleName
                    val missingDependencies = enabledPlugin.dependencies.asSequence()
                        .filterNot { it.javaClass in enabledPlugins }
                        .joinToString { it.javaClass.simpleName }
                    log.warn("插件 $pluginName 缺少以下依赖 $missingDependencies")
                }
                break
            }
            val key = plugin.javaClass
            val redirect = if (plugin is EventEmitter) plugin else DefaultEventEmitter()
            pluginEventRegistry.register(key, redirect)
            try {
                emitter.emit(PluginEvent.BeforeEnable, PluginEventArgs(this, plugin))
                plugin.enabled(this)
                plugin.registerEvent(redirect)
                plugin.registerHook(this)
                emitter.emit(PluginEvent.Enabled, PluginEventArgs(this, plugin))
                emitter.targetTo(redirect)
                enabledPlugins.add(plugin.javaClass)
            } catch (e: Exception) {
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
            emitter.emit(PluginEvent.Destroyed, PluginEventArgs(this, plugin))
        }
        pluginRegistry.remove(key)
        pluginEventRegistry.remove(key)
    }

    override fun <T : Plugin> getPlugin(key: Class<T>) = pluginRegistry.get(key).cast<T>()
    override fun getAllPlugins() = pluginRegistry.all().toList()
    override fun <T : Plugin> hasPlugin(key: Class<T>) = pluginRegistry.have(key)


}
