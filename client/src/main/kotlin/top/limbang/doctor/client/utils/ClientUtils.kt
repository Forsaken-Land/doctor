package top.limbang.doctor.client.utils

import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.handler.packetEvent
import top.limbang.doctor.protocol.api.Packet
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

inline fun <reified T : Packet> Connection.sendAndWait(packet: Packet, timeout: Long): T {
    this.sendPacket(packet)
    return this.emitter.asSingle(packetEvent<T>()).timeout(timeout, TimeUnit.MILLISECONDS).blockingGet()
}