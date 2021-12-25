package top.fanua.doctor.plugin.forge.codec

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.plugin.forge.api.FML1Packet
import top.fanua.doctor.plugin.forge.forgeProtocolState
import top.fanua.doctor.plugin.forge.registry.IFML1PacketRegistry
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.play.client.CustomPayloadPacket

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class Forge1PacketHandler(
    val emitter: EventEmitter,
    private val channelRegistry: IFML1PacketRegistry
) : MessageToMessageCodec<Packet, FML1Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(Forge1PacketHandler::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: FML1Packet, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()
        try {
            val encoder = channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState())
                .encoder(msg.javaClass)
            val channel =
                channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState()).packetKey(msg.javaClass)
            encoder.encode(buf, msg)
            out.add(CustomPayloadPacket(channel, buf))
            logger.debug("协议包编码:channel=$channel $msg")
        } catch (e: Exception) {
            logger.warn(e.message)
            return
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        if (msg is CustomPayloadPacket && !msg.processed && channelRegistry.channels.contains(msg.channel)) {
            val packet: FML1Packet
            try {
                val decoder = channelRegistry.channelPacketMap(PacketDirection.S2C, ctx.forgeProtocolState())
                    .decoder<FML1Packet>(msg.channel)
                packet = decoder.decoder(msg.data)
                msg.close()
//                        emitter.emit(PacketEvent(packet.javaClass.kotlin), packet)
//                emitPacketEvent(emitter, packet, ctx)
                out.add(packet)
            } catch (e: Exception) {
                logger.warn(e.message)
                return
            }
            logger.debug("协议包解码:channel=${msg.channel} $packet")
            //包没有处理，交给下一个codec
        } else ctx.fireChannelRead(msg)


    }

}
