package top.limbang.doctor.plugin.forge.codec

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.plugin.forge.forgeProtocolState
import top.limbang.doctor.plugin.forge.registry.IChannelPacketRegistry
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.definition.play.client.CustomPayloadPacket

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class ForgePacketHandler(
    val emitter: EventEmitter,
    val channelRegistry: IChannelPacketRegistry
) : SimpleChannelInboundHandler<Packet>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
        var handled = false
        when (msg) {
            is CustomPayloadPacket -> {
                ctx.forgeProtocolState()
                ctx.fireChannelReadComplete()
                handled = true
            }
        }
        //包没有处理，交给下一个codec
        if (!handled) {
            ctx.fireChannelRead(msg)
        }
    }
}