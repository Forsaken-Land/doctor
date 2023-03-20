package top.fanua.doctor.client.utils

import top.fanua.doctor.core.api.event.Event
import top.fanua.doctor.network.api.Connection
import top.fanua.doctor.network.handler.packetEvent
import top.fanua.doctor.protocol.api.Packet
import java.util.concurrent.TimeUnit

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */


inline fun <reified T : Packet> Connection.sendAndWait(packet: Packet): T {
    this.sendPacket(packet)
    return this.emitter.asSingle(packetEvent<T>()).blockingGet()
}

fun <T : Any> Connection.sendAndWait(event: Event<T>, packet: Packet): T {
    this.sendPacket(packet)
    return this.emitter.asSingle(event).blockingGet()
}


inline fun <reified T : Packet> Connection.sendAndWait(packet: Packet, timeout: Long): T {
    this.sendPacket(packet)
    return this.emitter.asSingle(packetEvent<T>()).timeout(timeout, TimeUnit.MILLISECONDS).blockingGet()
}
