package top.fanua.doctor.plugin.forge.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.plugin.forge.definations.fml2.LoginWrapperDecoder
import top.fanua.doctor.plugin.forge.definations.fml2.LoginWrapperEncoder
import top.fanua.doctor.plugin.forge.definations.fml2.LoginWrapperPacket
import top.fanua.doctor.plugin.forge.registry.ChannelAndId
import top.fanua.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.login.client.LoginPluginResponsePacket
import top.fanua.doctor.protocol.definition.login.server.LoginPluginRequestPacket
import top.fanua.doctor.protocol.extension.readVarInt
import top.fanua.doctor.protocol.extension.writeVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/30:15:51
 */
class Forge2PacketHandler(
    val emitter: EventEmitter,
    val fmL2PacketRegistry: IFML2PacketRegistry
) : MessageToMessageCodec<Packet, FML2Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(Forge2PacketHandler::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: FML2Packet, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()
        val outBuf = ctx.alloc().buffer()
        try {

            //写FML2包id+包
            val packetEncoder = fmL2PacketRegistry.fml2PacketMap(PacketDirection.C2S).encoder(msg.javaClass)
            val channelAndId = fmL2PacketRegistry.fml2PacketMap(PacketDirection.C2S).packetKey(msg.javaClass)
            val packetId = channelAndId.id
            val channel = channelAndId.channel
            buf.writeVarInt(packetId)
            packetEncoder.encode(buf, msg)

            //写LoginWrapperEncoder包
            val encoder = LoginWrapperEncoder()
            val loginWrapperPacket = LoginWrapperPacket(channel, outBuf)
            encoder.encode(outBuf, loginWrapperPacket)

            //写FML2包长度
            outBuf.writeVarInt(buf.readableBytes())
            outBuf.writeBytes(buf)
            buf.release()
            out.add(LoginPluginResponsePacket(msg.messageId, true, outBuf))
            logger.debug("FML2协议包编码:id=$packetId $msg")
        } catch (e: Exception) {
            logger.warn(e.message)
            return
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        if (msg is LoginPluginRequestPacket && msg.channel == "fml:loginwrapper") {
            try {
                val packet: FML2Packet
                val decoder = LoginWrapperDecoder()
                packet = decoder.decoder(msg.data)
                val channel = packet.resourceLocation
                val buf = readVarIntLengthBasedFrame(ctx, packet.data)
                val packerId = buf.readVarInt()
                val packetDecoder = fmL2PacketRegistry.fml2PacketMap(PacketDirection.S2C).decoder<FML2Packet>(
                    ChannelAndId(channel, packerId)
                )
                val packetInPacket = packetDecoder.decoder(buf)
                buf.release()
                packetInPacket.messageId = msg.messageId
                msg.close()
                packet.close()
                out.add(packetInPacket)
                logger.debug("FML2协议包解码:packetId=${packerId} $packetInPacket")
            } catch (e: Exception) {
                logger.debug(e.message)
                logger.debug("尝试自动发送")
                out.add(msg)
            }

        } else ctx.fireChannelRead(msg)


    }

    private fun readVarIntLengthBasedFrame(ctx: ChannelHandlerContext, msg: ByteBuf): ByteBuf {
        val remainingPacketLength = msg.readVarInt()
        val newBuf = ctx.alloc().buffer(remainingPacketLength)
        msg.readBytes(newBuf, remainingPacketLength)
        msg.release()
        return newBuf
    }


}

