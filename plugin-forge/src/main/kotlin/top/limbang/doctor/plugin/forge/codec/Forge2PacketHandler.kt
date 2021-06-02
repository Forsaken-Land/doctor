package top.limbang.doctor.plugin.forge.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.network.handler.emitPacketEvent
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.plugin.forge.definations.fml2.LoginWrapperDecoder
import top.limbang.doctor.plugin.forge.definations.fml2.LoginWrapperEncoder
import top.limbang.doctor.plugin.forge.definations.fml2.LoginWrapperPacket
import top.limbang.doctor.plugin.forge.registry.IFML2PacketRegistry
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
    val fmL2PacketRegistry: IFML2PacketRegistry
) : MessageToMessageCodec<Packet, FML2Packet>() {

    private val logger: Logger = LoggerFactory.getLogger(Forge1PacketHandler::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: FML2Packet, out: MutableList<Any>) {
        val buf = ctx.alloc().buffer()
        val outBuf = ctx.alloc().buffer()
        try {

            //写LoginWrapperEncoder包
            val encoder = LoginWrapperEncoder()
            val loginWrapperPacket = LoginWrapperPacket("fml:handshake", outBuf)
            encoder.encode(outBuf, loginWrapperPacket)

            //写FML2包id+包
            val packetEncoder = fmL2PacketRegistry.fml2PacketMap(PacketDirection.C2S).encoder(msg.javaClass)
            val packetId = fmL2PacketRegistry.fml2PacketMap(PacketDirection.C2S).packetKey(msg.javaClass)
            buf.writeVarInt(packetId)
            packetEncoder.encode(buf, msg)

            //写FML2包长度
            outBuf.writeVarInt(buf.readableBytes())
            outBuf.writeBytes(buf)

            out.add(LoginPluginResponsePacket(msg.messageId, true, outBuf))
            logger.debug("FML2协议包编码:id=$packetId $msg")
            loginWrapperPacket.close()
        } catch (e: Exception) {
            logger.warn(e.message)
            return
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        if (msg is LoginPluginRequestPacket) {
            try {
                if (msg.channel == "fml:loginwrapper") {
                    val packet: FML2Packet
                    val decoder = LoginWrapperDecoder()
                    packet = decoder.decoder(msg.data)
                    val buf = readVarIntLengthBasedFrame(ctx, packet.data)
                    val packerId = buf.readVarInt()
                    val packetDecoder =
                        fmL2PacketRegistry.fml2PacketMap(PacketDirection.S2C)
                            .decoder<FML2Packet>(packerId)
                    val packetInPacket = packetDecoder.decoder(buf)
                    packetInPacket.messageId = msg.messageId
                    logger.debug("FML2协议包解码:packetId=${packerId} $packetInPacket")
                    emitPacketEvent(emitter, packetInPacket, ctx)
                    ctx.fireChannelReadComplete()
                    msg.close()
                    packet.close()
                    return
                } else {
                    logger.info("LoginPluginRequest协议包需要解码:channel=${msg.channel}")
                }
            } catch (e: Exception) {
                logger.warn(e.message)
                return
            }

        } else ctx.fireChannelRead(msg)


    }

    private fun readVarIntLengthBasedFrame(ctx: ChannelHandlerContext, msg: ByteBuf): ByteBuf {
        val remainingPacketLength = msg.readVarInt()
        val newBuf = ctx.alloc().buffer(remainingPacketLength)
        msg.readBytes(newBuf, remainingPacketLength)
        return newBuf
    }


}

