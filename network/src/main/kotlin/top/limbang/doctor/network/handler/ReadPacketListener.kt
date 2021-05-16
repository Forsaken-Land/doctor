package top.limbang.doctor.network.handler

import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.protocol.api.Packet
import kotlin.reflect.KClass

/**
 *
 * @author WarmthDawn
 * @since 2021-05-13
 */
class ReadPacketListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Read) {
            if (it.message is Packet) {
                emitter.emit(PacketEvent(it.message.javaClass.kotlin), it.message)
                emitter.emit(
                    WrappedPacketEvent(it.message.javaClass.kotlin),
                    WrappedPacketEventArgs(it.context!!, it.message)
                )
            }
        }
    }
}

data class WrappedPacketEventArgs<T : Packet>(val ctx: ChannelHandlerContext, val packet: T)
class WrappedPacketEvent<T : Packet>(val type: KClass<T>) : Event<WrappedPacketEventArgs<T>> {
    override fun equals(other: Any?): Boolean {
        return other is WrappedPacketEvent<*> && other.type == type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}

class PacketEvent<T : Packet>(val type: KClass<T>) : Event<T> {
    override fun equals(other: Any?): Boolean {
        return other is PacketEvent<*> && other.type == type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}