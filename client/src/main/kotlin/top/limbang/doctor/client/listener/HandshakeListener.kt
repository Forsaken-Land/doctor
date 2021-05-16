package top.limbang.doctor.client.listener

import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.protocol.definition.client.HandshakePacket

class HandshakeListener : EventListener {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val connectionKey = AttributeKey.valueOf<Connection>("connection")

    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Connected) {
            log.debug("Handshake:${it.context!!.channel().remoteAddress()}")
            val connection = it.context!!.channel().attr(connectionKey).get()
            connection.sendPacket(
                HandshakePacket(0, connection.host, connection.port, 1)
            )

        }


    }
}