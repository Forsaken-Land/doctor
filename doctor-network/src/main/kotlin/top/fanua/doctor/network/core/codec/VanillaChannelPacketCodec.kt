package top.fanua.doctor.network.core.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import top.fanua.doctor.protocol.api.ChannelPacket
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.play.client.CustomPayloadPacket
import top.fanua.doctor.protocol.registry.IChannelPacketRegistry

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/3 下午3:46
 */
class VanillaChannelPacketCodec(
    private val protocol: IChannelPacketRegistry,
    private val encodeDirection: PacketDirection,
    private val decodeDirection: PacketDirection
) : MessageToMessageCodec<Packet, Packet>() {
    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        if (msg !is CustomPayloadPacket) {
            lateinit var buf : ByteBuf
            try {
                buf = ctx.alloc().buffer()
                val encoder = protocol.packetMap(encodeDirection).encoder(msg.javaClass)
                val channel = protocol.packetMap(encodeDirection).packetKey(msg.javaClass)
                out.add(CustomPayloadPacket(channel, encoder.encode(buf, msg)))
            } catch (e: Exception) {
                buf.release()
                out.add(msg)
            }
        } else {
            out.add(msg)
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        if (msg is CustomPayloadPacket) {
            try {
                val decoder = protocol.packetMap(decodeDirection).decoder<ChannelPacket>(msg.channel)
                val packet = decoder.decoder(msg.data)
                msg.close()
                out.add(packet)
            } catch (e: Exception) {
                ctx.fireChannelRead(msg)
            }
        } else {
            ctx.fireChannelRead(msg)
        }
    }

}
