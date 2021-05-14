package top.limbang.doctor.core.api.plugin

import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter

/**
 * 表示一个插件的入口
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface Plugin {
    /**
     * 初始化插件
     */
    fun created()

    /**
     * 销毁插件
     */
    fun destroy()

    /**
     * 注册事件
     */
    fun registerEvent(emitter: EventEmitter)

    /**
     * 注册钩子
     */
    fun hookProvider(registry: IHookProviderRegistry)
}