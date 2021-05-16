package top.limbang.doctor.core.impl.event

import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventHandler
import java.time.Instant

/**
 * 事件触发器的默认实现
 * @author WarmthDawn
 * @since 2021-05-13
 */
class DefaultEventEmitter : EventEmitter {
    private val listeners = HashMap<Event<*>, HashSet<EventHandler<*>>>()
    private val onceListeners = HashMap<Event<*>, HashSet<EventHandler<*>>>()
    private val durationListeners = HashMap<Event<*>, HashSet<Pair<Long, EventHandler<*>>>>()
    private val targets = HashSet<EventEmitter>()

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
            listeners[event] = HashSet()
        }
        listeners[event]?.add(handler)
        return this
    }

    override fun <T> once(event: Event<T>, handler: EventHandler<T>): EventEmitter {
        if (!onceListeners.containsKey(event)) {
            onceListeners[event] = HashSet()
        }
        onceListeners[event]?.add(handler)
        return this
    }

    override fun <T> until(event: Event<T>, until: Instant, handler: EventHandler<T>): DefaultEventEmitter {
        if (!durationListeners.containsKey(event)) {
            durationListeners[event] = HashSet()
        }
        durationListeners[event]?.add(Pair(until.epochSecond, handler))
        return this
    }

    private fun <T> handleEvent(handler: EventHandler<*>, args: T) {
        handler.cast<T>().handle(args)
//        handler.cast<T>()(args)
    }

    override fun <T> emit(event: Event<T>, args: T): EventEmitter {
        //普通事件
        listeners[event]?.forEach {
            handleEvent(it, args)
        }

        //一次性事件
        onceListeners[event]?.forEach {
            handleEvent(it, args)
        }
        onceListeners[event]?.clear()

        clearExpire(event)
        //计时事件
        durationListeners[event]?.forEach {
            handleEvent(it.second, args)
        }

        targets.forEach {
            if (it != this) {
                it.emit(event, args)
            }
        }
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
        targets.add(another)
    }

    override fun removeTarget(another: EventEmitter) {
        targets.remove(another)
    }

}