package top.limbang.doctor.plugin.forge.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.network.handler.emitPacketEvent
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.plugin.forge.definations.fml2.LoginWrapperPacket
import top.limbang.doctor.plugin.forge.forgeProtocolState
import top.limbang.doctor.plugin.forge.registry.IChannelPacketRegistry
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.definition.login.client.LoginPluginResponsePacket
import top.limbang.doctor.protocol.definition.login.server.LoginPluginRequestPacket
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/30:15:51
 */
class Forge2PacketHandler(
    val emitter: EventEmitter,
    val channelRegistry: IChannelPacketRegistry
) : MessageToMessageCodec<Packet, FML2Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(Forge1PacketHandler::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: FML2Packet, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()
        val outBuf = ctx.alloc().buffer()
        try {
            val packetEncoder = channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState())
                .encoder(msg.javaClass)
            val packetId =
                channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState()).packetKey(msg.javaClass)

            packetEncoder.encode(buf, msg)
            val encoder = channelRegistry.channelPacketMap(PacketDirection.C2S, ctx.forgeProtocolState())
                .encoder(LoginWrapperPacket::class.java)
            val loginWrapperPacket = LoginWrapperPacket("fml:handshake", outBuf)
            encoder.encode(outBuf, loginWrapperPacket)
            outBuf.writeVarInt(buf.readableBytes() + 1)
            outBuf.writeVarInt(packetId.toInt())
            outBuf.writeBytes(buf)
            out.add(LoginPluginResponsePacket(msg.messageId, true, outBuf))
            logger.debug("Forge协议包编码:id=$packetId $msg")
        } catch (e: Exception) {
            logger.warn(e.message)
            return
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        if (msg is LoginPluginRequestPacket) {
            val packet: FML2Packet
            try {
                val decoder = channelRegistry.channelPacketMap(PacketDirection.S2C, ctx.forgeProtocolState())
                    .decoder<FML2Packet>(msg.channel)
                packet = decoder.decoder(msg.data)
                msg.close()
                logger.debug("Channel协议包解码:channel=${msg.channel} $packet")
                if (packet is LoginWrapperPacket) {
                    val buf = readVarIntLengthBasedFrame(ctx, packet.data)
                    val packerId = buf.readVarInt()
                    val packetDecoder = channelRegistry.channelPacketMap(PacketDirection.S2C, ctx.forgeProtocolState())
                        .decoder<FML2Packet>(packerId.toString())
                    val packetInPacket = packetDecoder.decoder(buf)
                    packetInPacket.messageId = msg.messageId
                    logger.debug("Forge协议包解码:packetId=${packerId} $packetInPacket")
                    emitPacketEvent(emitter, packetInPacket, ctx)
                    ctx.fireChannelReadComplete()
                    return
                }
                emitPacketEvent(emitter, packet, ctx)
                ctx.fireChannelReadComplete()
            } catch (e: Exception) {
                logger.warn(e.message)
                return
            }
            logger.debug("协议包解码:channel=${msg.channel} $packet")
        } else ctx.fireChannelRead(msg)


    }

    private fun readVarIntLengthBasedFrame(ctx: ChannelHandlerContext, msg: ByteBuf): ByteBuf {
        val remainingPacketLength = msg.readVarInt()
        val newBuf = ctx.alloc().buffer(remainingPacketLength)
        msg.readBytes(newBuf, remainingPacketLength)
        return newBuf
    }

}

