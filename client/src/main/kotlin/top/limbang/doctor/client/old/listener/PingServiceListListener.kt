package top.limbang.minecraft.core.listener

import io.netty.channel.ChannelHandlerContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.WrappedPacketEvent
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.protocolState
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.status.client.PingPacket
import top.limbang.doctor.protocol.definition.status.client.RequestPacket
import top.limbang.doctor.protocol.definition.status.server.PongPacket
import top.limbang.doctor.protocol.definition.status.server.ResponsePacket
import java.util.concurrent.TimeUnit

open class PingServiceListListener : EventListener {
    private val logger: Logger = LoggerFactory.getLogger(PingServiceListListener::class.java)
    private val connectionKey = Attributes.ATTR_CONNECTION

    override fun initListen(emitter: EventEmitter) {
        emitter.on(WrappedPacketEvent(ResponsePacket::class)) { (ctx, packet) ->
            val connection = ctx.channel().attr(connectionKey).get()
            response(packet, connection)
        }

        emitter.on(WrappedPacketEvent(PongPacket::class)) { (ctx, packet) ->
            val connection = ctx.channel().attr(connectionKey).get()
            pong(packet, connection)
        }
        emitter.on(ConnectionEvent.Connected) {
            request(it.context!!)
        }
    }

    /**
     * ### 发送状态请求包
     *
     * 提交一个300毫秒后的任务,如果状态等于[ProtocolState.STATUS]就发送请求包
     */
    private fun request(ctx: ChannelHandlerContext) {
        val connection = ctx.channel().attr(connectionKey).get()
        ctx.executor().schedule({
            if (ctx.protocolState() == ProtocolState.STATUS)
                connection.sendPacket(RequestPacket())
        }, 300, TimeUnit.MICROSECONDS)
    }


    /**
     * ### 状态响应包
     *
     *
     */
    open fun response(packet: ResponsePacket, connection: Connection) {
        // 发送 ping
        connection.sendPacket(PingPacket(System.currentTimeMillis()))
    }

    /**
     * ### pong 包
     *
     *
     */
    open fun pong(packet: PongPacket, connection: Connection) {
        logger.info("服务器延迟:${System.currentTimeMillis() - packet.payload}")
        if (!connection.isClosed()) connection.close()
    }
}