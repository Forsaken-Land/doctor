package top.fanua.doctor.core.api.plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
interface PluginHookHandler<T : IHookMessage> {
    val owner: Plugin
    val priority: Int
    val messageType: Class<T>
    fun handle(message: T): Boolean
}

class PluginHookHandlerImpl<T : IHookMessage>(
    override val owner: Plugin,
    override val priority: Int,
    val handler: (T) -> Boolean,
    override val messageType: Class<T>
) : PluginHookHandler<T> {
    override fun handle(message: T): Boolean = handler(message)
}

inline fun <reified T : IHookMessage> Plugin.createHandler(
    priority: Int = 10,
    noinline handler: (T) -> Boolean
): PluginHookHandler<T> {
    return PluginHookHandlerImpl(this, priority, handler, T::class.java)
}


interface IHookMessage {
    val isCoR: Boolean
}


open class NotifyHookMessage(
    override val isCoR: Boolean
) : IHookMessage

/**
 * ## 钩子的消息
 * [message] 需要处理的消息
 * [isCoR] 是否是责任链模型
 */
open class HookMessage<T>(
    val message: T,
    override val isCoR: Boolean = false
) : IHookMessage

/**
 * ## 可编辑的钩子的消息
 * [message] 需要处理的消息
 * [edited] 消息是否被处理过
 */
open class MutableHookMessage<T>(
    var message: T,
    override val isCoR: Boolean = false
) : IHookMessage {
    var edited: Boolean = false
}
