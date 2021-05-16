package top.limbang.doctor.client.old.listener

import io.netty.channel.ChannelHandlerContext
import io.netty.util.AttributeKey
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ProtocolStateChange
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.connection
import top.limbang.doctor.network.utils.protocolState
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.play.client.CKeepAlivePacket
import top.limbang.doctor.protocol.definition.play.client.SKeepAlivePacket
import top.limbang.doctor.protocol.definition.status.server.ResponsePacket
import top.limbang.doctor.protocol.entity.FML1SimpleServiceResponse
import top.limbang.doctor.protocol.entity.SimpleServiceResponse


open class HandshakeListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Connected) {
            handshake(it.context!!)
        }
        emitter.on(ConnectionEvent.Read) {
            when (it.message) {
                is ResponsePacket ->{
                    getServiceResponse(it.message as ResponsePacket)
                    val connection = it.context!!.connection()
                    val version = serviceResponse.version.protocol
                    connection.sendPacket(
                        HandshakePacket(
                            version,
                            connection.host,
                            connection.port,
                            ProtocolState.LOGIN
                        )
                    ).await()
                }
                is SKeepAlivePacket -> {
                    keepAlive(
                        it.context!!.channel().attr(Attributes.ATTR_CONNECTION).get(),
                        it.message as SKeepAlivePacket
                    )
                }
            }
        }
    }

    private val connectionKey = AttributeKey.valueOf<Connection>("connection")
    private lateinit var serviceResponse: SimpleServiceResponse


    private fun keepAlive(connection: Connection, packet: SKeepAlivePacket) {
        connection.sendPacket(CKeepAlivePacket(packet.keepAliveId))
    }

    private fun getServiceResponse(packet: ResponsePacket) {
        val json = Json { ignoreUnknownKeys = true }
        serviceResponse = json.decodeFromString<FML1SimpleServiceResponse>(packet.json)
    }

    private fun handshake(ctx: ChannelHandlerContext) {
        val connectionAttr = ctx.channel().attr(connectionKey)
        val connection = connectionAttr.get()
        when (ctx.protocolState()) {
            ProtocolState.HANDSHAKE -> {
                // 发送握手包
                connection.sendPacket(HandshakePacket(0, connection.host, connection.port, ProtocolState.STATUS))
                    .await()
                // 更改连接状态为: 状态
                ctx.setProtocolState(ProtocolState.STATUS)
            }
            else -> return
        }
        connectionAttr.set(connection)
    }
}