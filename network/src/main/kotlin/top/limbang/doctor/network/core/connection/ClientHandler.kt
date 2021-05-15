package top.limbang.doctor.network.core.connection

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/14:20:41
 */
/**
 * ### 客户端处理
 *
 * 通知断线重连等事件
 */
class ClientHandler(private val emitter: EventEmitter) : ChannelInboundHandlerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(ClientHandler::class.java)

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.debug("连接成功:${ctx.channel().remoteAddress()}")
        emitter.emit(ConnectionEvent.Connected, ConnectionEventArgs(ctx))
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.debug("连接断开:${ctx.channel().remoteAddress()}")
        emitter.emit(ConnectionEvent.Disconnect, ConnectionEventArgs(ctx))
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        emitter.emit(ConnectionEvent.Read, ConnectionEventArgs(ctx, msg))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("客户端异常事件:${cause.message}", cause)
        ctx.close()
        emitter.emit(ConnectionEvent.Error, ConnectionEventArgs(ctx, error = cause))
    }
}