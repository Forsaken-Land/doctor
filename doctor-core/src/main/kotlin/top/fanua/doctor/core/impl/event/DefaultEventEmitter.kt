package top.fanua.doctor.core.impl.event

import org.slf4j.LoggerFactory
import top.fanua.doctor.core.api.event.Event
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventHandler
import top.fanua.doctor.core.setTimeout
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

typealias  ConcurrentHashSet<E> = ConcurrentHashMap.KeySetView<E, Boolean>

fun <E> newConcurrentHashSet(): ConcurrentHashSet<E> = ConcurrentHashMap.newKeySet()

/**
 * 事件触发器的默认实现
 * @author WarmthDawn
 * @since 2021-05-13
 */
class DefaultEventEmitter : EventEmitter {
    companion object {
        val logger = LoggerFactory.getLogger(DefaultEventEmitter::class.java)
    }

    private val listeners = ConcurrentHashMap<Event<*>, ConcurrentHashSet<EventHandler<*>>>()
    private val onceListeners = ConcurrentHashMap<Event<*>, ConcurrentHashSet<EventHandler<*>>>()
    private val durationListeners = ConcurrentHashMap<Event<*>, ConcurrentHashSet<Pair<Long, EventHandler<*>>>>()
    private val targets: ConcurrentHashSet<EventEmitter> = newConcurrentHashSet()
    private var passive = false

    private fun clearExpire() {
        val now = Instant.now().epochSecond
        durationListeners.values.forEach {
            it.removeIf { (until, _) ->
                until <= now
            }
        }

    }

    private fun clearExpire(key: Event<*>) {
        val now = Instant.now().epochSecond
        durationListeners[key]?.removeIf { (until, _) ->
            until <= now
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> EventHandler<*>.cast(): EventHandler<T> = this as EventHandler<T>

    override fun <T> on(event: Event<T>, handler: EventHandler<T>): EventEmitter {
        if (!listeners.containsKey(event)) {
            listeners[event] = newConcurrentHashSet()
        }
        listeners[event]?.add(handler)
        return this
    }

    override fun <T> once(event: Event<T>, handler: EventHandler<T>): EventEmitter {
        if (!onceListeners.containsKey(event)) {
            onceListeners[event] = newConcurrentHashSet()
        }
        onceListeners[event]?.add(handler)
        return this
    }

    override fun <T> until(event: Event<T>, until: Instant, handler: EventHandler<T>): DefaultEventEmitter {
        if (!durationListeners.containsKey(event)) {
            durationListeners[event] = newConcurrentHashSet()
        }
        durationListeners[event]?.add(Pair(until.epochSecond, handler))
        return this
    }

    private fun <T> handleEvent(handler: EventHandler<*>, args: T) {
        handler.cast<T>().handle(args)
//        handler.cast<T>()(args)
    }


    override fun <T> emit(event: Event<T>, args: T, sources: List<EventEmitter>): EventEmitter {
        val timeout = setTimeout(1000 * 5) { logger.warn("事件 $event 执行超过5秒") }
        //普通事件
        listeners[event]?.toList()?.forEach {
            handleEvent(it, args)
        }

        //一次性事件
        onceListeners[event]?.toList()?.forEach {
            handleEvent(it, args)
        }
        onceListeners[event]?.clear()

        clearExpire(event)
        //计时事件
        durationListeners[event]?.toList()?.forEach {
            handleEvent(it.second, args)
        }

        if(targets.size > 0){
            val newSource = sources.toMutableList()
            newSource.add(this)
            targets.forEach {
                if (it !in sources) {
                    it.emit(event, args, newSource)
                }
            }
        }

        timeout.cancel()
        return this
    }

    override fun <T> remove(event: Event<T>, handler: EventHandler<T>): DefaultEventEmitter {
        listeners[event]?.remove(handler)
        onceListeners[event]?.remove(handler)
        durationListeners[event]?.removeIf {
            it.second == handler
        }
        return this
    }

    override fun <T> removeAll(event: Event<T>): EventEmitter {
        listeners[event]?.clear()
        onceListeners[event]?.clear()
        durationListeners[event]?.clear()
        return this
    }

    override fun removeAll(): EventEmitter {
        listeners.clear()
        onceListeners.clear()
        durationListeners.clear()
        return this
    }

    override fun <T> handlers(event: Event<T>): List<EventHandler<T>> {
        val result: MutableSet<EventHandler<T>> = linkedSetOf()
        listeners[event]?.forEach {
            result.add(it.cast())
        }
        onceListeners[event]?.forEach {
            result.add(it.cast())
        }
        clearExpire(event)
        durationListeners[event]?.forEach {
            result.add(it.second.cast())
        }
        return result.toList()
    }

    override fun handlers(): List<EventHandler<*>> {
        val result: MutableSet<EventHandler<*>> = linkedSetOf()
        listeners.values.flatten().forEach {
            result.add(it)
        }
        onceListeners.values.flatten().forEach {
            result.add(it)
        }
        clearExpire()
        durationListeners.values.flatten().forEach {
            result.add(it.second)
        }
        return result.toList()
    }

    override fun targetTo(another: EventEmitter) {
        if (passive) {
            logger.warn("尝试向一个Passive的事件处理器添加事件重定向")
        }
        targets.add(another)
    }

    override fun removeTarget(another: EventEmitter) {
        targets.remove(another)
    }

}
