package top.fanua.doctor.core.api.plugin

import top.fanua.doctor.core.api.event.EventEmitter

/**
 * 表示一个插件的入口
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface Plugin {

    val dependencies: List<Class<out Plugin>> get() = emptyList()
    /**
     * 初始化插件
     */
    fun created(manager: IPluginManager) {}

    /**
     * 启动插件
     */
    fun enabled(manager: IPluginManager) {}

    /**
     * 销毁插件
     */
    fun destroy() {}

    /**
     * 注册事件
     * [emitter]是一个独立的事件触发器，在上面触发事件不会影响到发布者，仅会单向接收
     * 如果希望同步事件，请自行调用 [EventEmitter.targetTo] 方法
     */
    fun registerEvent(emitter: EventEmitter) {}

    /**
     * 注册钩子
     * [registry] 提供已经注册的钩子的查询接口
     */
    fun registerHook(manager: IPluginHookManager) {}
}
