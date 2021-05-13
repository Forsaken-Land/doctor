package top.limbang.doctor.core.api.event

/**
 * 事件监听器，用于一次性注册多个事件处理器
 * @author WarmthDawn
 * @since 2021-05-13
 */
interface EventListener {
    fun initListen(emitter: EventEmitter)
}