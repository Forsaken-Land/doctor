package top.limbang.doctor.network.handler

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
            }
        }
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