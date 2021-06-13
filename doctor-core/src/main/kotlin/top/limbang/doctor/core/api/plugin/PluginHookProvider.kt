package top.limbang.doctor.core.api.plugin

import top.limbang.doctor.core.plugin.PluginHookRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

interface IPluginHookProvider<T : IHookMessage>

interface IPluginHookManager {
    /**
     * 触发插件钩子
     * [provider] 钩子提供者
     * [args] 要处理的消息
     * [freezeHook] 是否在本次处理后冻结钩子
     */
    fun <T : IHookMessage> invokeHook(provider: IPluginHookProvider<T>, args: T, freezeHook: Boolean = false)

    fun <T : IHookMessage> getHook(provider: IPluginHookProvider<T>): PluginHookRegistry<T>

    fun <T : IHookMessage> removeHook(provider: IPluginHookProvider<T>)


}