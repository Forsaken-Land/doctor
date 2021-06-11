package top.limbang.doctor.core.api.event

import java.time.Duration
import java.time.Instant

/**
 * 事件触发器，任何实现次接口的类均可以触发事件
 * 推荐使用kotlin委托写法
 * class Foo : EventEmitter by DefaultEventEmitter()
 *
 * @author WarmthDawn
 * @since 2021-05-13
 */
interface EventEmitter {
    /**
     * 监听事件
     */
    fun <T> on(event: Event<T>, handler: EventHandler<T>): EventEmitter

    /**
     * 监听下一次事件
     */
    fun <T> once(event: Event<T>, handler: EventHandler<T>): EventEmitter

    /**
     * 监听一段时间内的事件
     */
    fun <T> during(event: Event<T>, duration: Duration, handler: EventHandler<T>): EventEmitter =
        until(event, Instant.now().plus(duration), handler)

    /**
     * 监听某个时间点之前的事件
     */
    fun <T> until(event: Event<T>, until: Instant, handler: EventHandler<T>): EventEmitter

    /**
     * 发布事件
     */
    fun <T> emit(event: Event<T>, args: T): EventEmitter

    fun emit(event: Event<Unit>): EventEmitter {
        return emit(event, Unit)
    }

    /**
     * 取消监听事件
     */
    fun <T> remove(event: Event<T>, handler: EventHandler<T>): EventEmitter

    /**
     * 取消监听所有事件
     */
    fun <T> removeAll(event: Event<T>): EventEmitter

    /**
     * 取消监听所有事件
     */
    fun removeAll(): EventEmitter

    /**
     * 获取某个事件的监听器
     */
    fun <T> handlers(event: Event<T>): List<EventHandler<T>>

    /**
     * 获取所有事件的监听器
     */
    fun handlers(): List<EventHandler<*>>

    /**
     * 添加通用监听器
     */
    fun addListener(listener: EventListener): EventEmitter {
        listener.initListen(this)
        return this
    }

    /**
     * 使当前事件触发器监听其他触发器的事件
     */
    fun listenTo(another: EventEmitter) = another.targetTo(this)

    /**
     * 使当前事件触发器同时触发其他触发器的事件
     */
    fun targetTo(another: EventEmitter)
    /**
     * 取消触发器同时触发
     */
    fun removeTarget(another: EventEmitter)

    val emitter: EventEmitter
        get() = this
}