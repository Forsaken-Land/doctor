package top.fanua.doctor.core.plugin

import org.slf4j.LoggerFactory
import top.fanua.doctor.core.api.plugin.IHookMessage
import top.fanua.doctor.core.api.plugin.IPluginHookManager
import top.fanua.doctor.core.api.plugin.IPluginHookProvider
import top.fanua.doctor.core.api.plugin.PluginHookHandler
import top.fanua.doctor.core.api.registry.Registry
import top.fanua.doctor.core.cast
import top.fanua.doctor.core.impl.registy.DefaultRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

class DefaultPluginHookManager : IPluginHookManager {
    companion object {
        val log = LoggerFactory.getLogger(PluginManager::class.java)
    }

    val registry: Registry<IPluginHookProvider<*>, PluginHookRegistry<*>> = DefaultRegistry()
    override fun <T : IHookMessage> invokeHook(
        provider: IPluginHookProvider<T>,
        args: T,
        freezeHook: Boolean
    ): Boolean {
        val hooks = registry.tryGet(provider) ?: return false
        if (hooks.frozen) {
            log.warn("尝试触发已经冻结的钩子，你是否错误的冻结了某个钩子？")
        }

        for (hook in hooks) {
            @Suppress("UNCHECKED_CAST")
            hook as PluginHookHandler<T>
            if (hook.handle(args) && args.isCoR) {
                return true
            }
        }

        if (freezeHook) {
            hooks.clear()
            hooks.freeze("钩子已经被锁定，无法注册")
        }
        return false
    }

    override fun <T : IHookMessage> getHook(provider: IPluginHookProvider<T>): PluginHookRegistry<T> {
        if (!registry.have(provider)) {
            registry.register(provider, PluginHookRegistry<IHookMessage>())
        }
        return registry.get(provider).cast()
    }

    override fun <T : IHookMessage> removeHook(provider: IPluginHookProvider<T>) {
        registry.remove(provider)
    }


}
