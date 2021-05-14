package top.limbang.doctor.core.plugin

import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.plugin.PluginHookProvider
import top.limbang.doctor.core.api.registry.Registry
import top.limbang.doctor.core.impl.registy.DefaultRegistry
import top.limbang.minecraft.api.cast

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class HookProviderRegistry : IHookProviderRegistry,
    Registry<Class<out PluginHookProvider<*>>, PluginHookProvider<*>> by DefaultRegistry() {
    override fun <T, V : PluginHookProvider<T>> provider(provider: Class<V>): V {
        if (!this.have(provider)) {
            this.register(provider, provider.newInstance())
        }
        return this.get(provider).cast()
    }

}