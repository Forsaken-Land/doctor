package top.limbang.doctor.network.handler

import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventHandler
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.utils.connection
import top.limbang.doctor.protocol.api.Packet
import kotlin.reflect.KClass

/**
 * ### 读取数据包监听器
 */
class ReadPacketListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Read) {
            if (it.message is Packet) {
                emitPacketEvent(emitter, it.message, it.context!!)
            }
        }
    }
}

/**
 * ### 数据包事件结果
 */
interface PacketEventResult<T : Packet> {
    fun reply(packet: Packet)
    fun reply(action: (T) -> Packet)
}

/**
 * ### 发出数据包事件
 */
fun emitPacketEvent(
    emitter: EventEmitter,
    message: Packet,
    ctx: ChannelHandlerContext
) {
    emitter.emit(PacketEvent(message.javaClass.kotlin), message)
    emitter.emit(
        WrappedPacketEvent(message.javaClass.kotlin),
        WrappedPacketEventArgs(ctx, message)
    )
}

/**
 * ### 监听指定数据包事件并处理
 */
inline fun <reified T : Packet> EventEmitter.onPacket(crossinline handler: WrappedPacketEventArgs<T>.() -> Unit): EventEmitter {
    return this.on(WrappedPacketEvent(T::class)) {
        handler(it)
    }
}

/**
 * ### 监听指定数据包并回复数据包
 */
inline fun <reified T : Packet> EventEmitter.replyPacket(crossinline reply: (T) -> Packet?): EventEmitter {
    this.on(WrappedPacketEvent(T::class)) {
        val rep = reply(it.packet)
        if (rep != null) {
            it.connection.sendPacket(rep)
        }
    }
    return this
}

/**
 * ### 监听指定数据包并回复一次数据包
 */
inline fun <reified T : Packet> EventEmitter.replyPacketOnce(crossinline reply: (T) -> Packet?): EventEmitter {
    return this.once(WrappedPacketEvent(T::class)) {
        val rep = reply(it.packet)
        if (rep != null) {
            it.connection.sendPacket(rep)
        }
    }
}

/**
 * ### 监听指定数据包并回复数据包
 */
inline fun <reified T : Packet> EventEmitter.replyPacket(vararg reply: Packet): EventEmitter {
    return this.on(WrappedPacketEvent(T::class)) { event ->
        reply.forEach {
            event.connection.sendPacket(it)
        }
    }
}

/**
 * ### 监听指定数据包并回复一次数据包
 */
inline fun <reified T : Packet> EventEmitter.replyPacketOnce(vararg reply: Packet): EventEmitter {
    return this.once(WrappedPacketEvent(T::class)) { event ->
        reply.forEach {
            event.connection.sendPacket(it)
        }
    }
}

/**
 * 当下一次接收到数据包触发
 */
inline fun <reified T : Packet> EventEmitter.oncePacket(crossinline handler: WrappedPacketEventArgs<T>.() -> Unit): EventEmitter {
    return this.once(WrappedPacketEvent(T::class)) {
        handler(it)
    }
}


/**
 * 当接收到数据包触发（参数包括ChannelHandlerContext)
 */
inline fun <reified T : Packet> EventEmitter.onPacketWrapped(handler: EventHandler<WrappedPacketEventArgs<T>>): EventEmitter {
    return this.on(WrappedPacketEvent(T::class), handler)
}

/**
 * 当下一次接收到数据包触发（参数包括ChannelHandlerContext)
 */
inline fun <reified T : Packet> EventEmitter.oncePacketWrapped(handler: EventHandler<WrappedPacketEventArgs<T>>): EventEmitter {
    return this.once(WrappedPacketEvent(T::class), handler)
}

data class WrappedPacketEventArgs<T : Packet>(val ctx: ChannelHandlerContext, val packet: T) {
    inline val connection get() = ctx.connection()
    fun sendPacket(packet: Packet) {
        connection.sendPacket(packet)
    }
}

/**
 * 接收到数据包事件，事件参数为数据包+ChannelHandlerContext
 */
class WrappedPacketEvent<T : Packet>(val type: KClass<T>) : Event<WrappedPacketEventArgs<T>> {
    override fun equals(other: Any?): Boolean {
        return other is WrappedPacketEvent<*> && other.type == type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}

/**
 * 接收到数据包事件，事件参数为数据包本身
 */
class PacketEvent<T : Packet>(val type: KClass<T>) : Event<T> {
    override fun equals(other: Any?): Boolean {
        return other is PacketEvent<*> && other.type == type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}
