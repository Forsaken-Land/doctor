package top.fanua.doctor.plugin.forge.codec

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.plugin.forge.api.ModPacket
import top.fanua.doctor.plugin.forge.registry.IModPacketRegistry
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.play.client.CustomPayloadPacket
import top.fanua.doctor.protocol.extension.readVarInt
import top.fanua.doctor.protocol.extension.writeVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/5:20:39
 */
class FML1ModPacketHandler(
    private val modRegistry: IModPacketRegistry
) : MessageToMessageCodec<CustomPayloadPacket, ModPacket>() {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun encode(ctx: ChannelHandlerContext, msg: ModPacket, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()
        try {
            val encoder = modRegistry.modPacketMap(msg.channel, PacketDirection.C2S).encoder(msg.javaClass)
            val discriminator = modRegistry.modPacketMap(msg.channel, PacketDirection.C2S).packetKey(msg.javaClass)
            val channel = msg.channel
            buf.writeVarInt(discriminator)
            encoder.encode(buf, msg)
            out.add(CustomPayloadPacket(channel, buf))
            log.debug("mod包编码: modChannel=$channel discriminator=$discriminator $msg")
        } catch (e: Exception) {

        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: CustomPayloadPacket, out: MutableList<Any>) {
        try {
            val packetId = msg.data.readVarInt()
            val decoder = modRegistry.modPacketMap(msg.channel, PacketDirection.S2C).decoder<ModPacket>(packetId)
            val packet = decoder.decoder(msg.data)
            msg.close()
            out.add(packet)
//            log.debug("mod包解码: modChannel=${msg.channel} discriminator=$packetId $packet")
        } catch (e: Exception) {
            if (msg.channel == "LagGoggles") log.debug("modChannel:${msg.channel} ${e.message}")
            log.trace("modChannel:${msg.channel} ${e.message}")
        }
    }

}
