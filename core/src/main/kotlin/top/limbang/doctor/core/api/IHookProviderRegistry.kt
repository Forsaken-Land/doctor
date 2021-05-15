package top.limbang.doctor.core.api

import top.limbang.doctor.core.api.plugin.PluginHookProvider

/**
 * 提供已经注册钩子的查询接口
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IHookProviderRegistry {
    /**
     * 查询注册的钩子
     */
    fun <T, V : PluginHookProvider<T>> provider(provider: Class<V>): PluginHookProvider<T>
}