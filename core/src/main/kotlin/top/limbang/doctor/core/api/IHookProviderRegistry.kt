package top.limbang.doctor.core.api

import top.limbang.doctor.core.api.plugin.PluginHookProvider

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IHookProviderRegistry {
    fun <T, V : PluginHookProvider<T>> provider(provider: Class<V>): PluginHookProvider<T>
}