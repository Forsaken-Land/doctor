package top.limbang.doctor.plugin.forge.codec

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.network.handler.emitPacketEvent
import top.limbang.doctor.plugin.forge.api.ChannelPacket
import top.limbang.doctor.plugin.forge.forgeProtocolState
import top.limbang.doctor.plugin.forge.registry.IChannelPacketRegistry
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.definition.play.client.CustomPayloadPacket

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class ForgePacketHandler(
    val emitter: EventEmitter,
    val channelRegistry: IChannelPacketRegistry
) : MessageToMessageCodec<Packet, ChannelPacket>() {

    private val logger: Logger = LoggerFactory.getLogger(ForgePacketHandler::class.java)

    //    override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
//        var handled = false
//        when (msg) {
//            is CustomPayloadPacket -> {
//                if (!msg.processed && channelRegistry.channels.contains(msg.channel)) {
//                    val packet: ChannelPacket
//                    try {
//                        val decoder = channelRegistry.channelPacketMap(PacketDirection.S2C, ctx.forgeProtocolState())
//                            .decoder<ChannelPacket>(msg.channel)
//                        packet = decoder.decoder(msg.rawData!!)
//                        emitter.emit(PacketEvent(packet.javaClass.kotlin), packet)
//                        emitter.emit(
//                            WrappedPacketEvent(packet.javaClass.kotlin),
//                            WrappedPacketEventArgs(ctx, packet)
//                        )
//                        ctx.fireChannelReadComplete()
//                        handled = true
//                    } catch (e: Exception) {
//                        logger.warn(e.message)
//                        return
//                    }
//                    logger.debug("协议包解码:channel=${msg.channel} $packet")
//                }
//            }
//        }
//        //包没有处理，交给下一个codec
//        if (!handled) {
//            ctx.fireChannelRead(msg)
//        }
//    }
    override fun encode(ctx: ChannelHandlerContext, msg: ChannelPacket, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()
        try {
            val encoder = channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState())
                .encoder(msg.javaClass)
            val channel =
                channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState()).packetKey(msg.javaClass)
            encoder.encode(buf, msg)
            out.add(CustomPayloadPacket(channel, hashMapOf(), buf))
            logger.debug("协议包编码:channel=$channel $msg")
        } catch (e: Exception) {
            logger.warn(e.message)
            return
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        var handled = false
        when (msg) {
            is CustomPayloadPacket -> {
                if (!msg.processed && channelRegistry.channels.contains(msg.channel)) {
                    val packet: ChannelPacket
                    try {
                        val decoder = channelRegistry.channelPacketMap(PacketDirection.S2C, ctx.forgeProtocolState())
                            .decoder<ChannelPacket>(msg.channel)

                        packet = decoder.decoder(msg.rawData!!)
                        msg.rawData?.release()
//                        emitter.emit(PacketEvent(packet.javaClass.kotlin), packet)
                        emitPacketEvent(emitter, packet, ctx)
                        ctx.fireChannelReadComplete()
                        handled = true
                    } catch (e: Exception) {
                        logger.warn(e.message)
                        return
                    }
                    logger.debug("协议包解码:channel=${msg.channel} $packet")
                }
            }
        }
        //包没有处理，交给下一个codec
        if (!handled) {
            ctx.fireChannelRead(msg)
        }

    }

}