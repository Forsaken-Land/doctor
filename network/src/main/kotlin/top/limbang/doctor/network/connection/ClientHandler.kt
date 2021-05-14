package top.limbang.doctor.network.connection

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.network.Client
import top.limbang.doctor.network.event.ReadPacketEventArgs
import top.limbang.doctor.network.handler.ConnectionSucceededEvent
import top.limbang.doctor.network.handler.DisconnectEvent
import top.limbang.doctor.network.handler.ReadPacketEvent
import top.limbang.doctor.protocol.api.Packet

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
class ClientHandler(private val client: Client) : ChannelInboundHandlerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(ClientHandler::class.java)

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.debug("连接成功:${ctx.channel().remoteAddress()}")

        client.emit(ConnectionSucceededEvent, ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        logger.debug("连接断开:${ctx.channel().remoteAddress()}")
        client.emit(DisconnectEvent, client)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        client.emit(ReadPacketEvent, ReadPacketEventArgs(msg as Packet, ctx.channel()))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("客户端异常事件:${cause.message}", cause)
        ctx.close()
        client.shutdown()
    }
}