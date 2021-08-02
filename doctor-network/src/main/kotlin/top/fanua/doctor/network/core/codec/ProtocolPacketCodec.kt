package top.fanua.doctor.network.core.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.network.utils.protocolState
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.core.ProtocolException
import top.fanua.doctor.protocol.definition.play.client.CustomPayloadPacket
import top.fanua.doctor.protocol.extension.readVarInt
import top.fanua.doctor.protocol.extension.writeVarInt
import top.fanua.doctor.protocol.registry.IPacketRegistry

/**
 * ### 协议包编解码
 *
 * - [protocol]协议版本
 */
class ProtocolPacketCodec(
    private val protocol: IPacketRegistry,
    private val encodeDirection: PacketDirection,
    private val decodeDirection: PacketDirection
) : MessageToMessageCodec<ByteBuf, Packet>() {
    private val logger: Logger = LoggerFactory.getLogger(ProtocolPacketCodec::class.java)
    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        val packetMap = protocol.packetMap(encodeDirection, ctx.protocolState())
        val buf = ctx.alloc().buffer()
        val packetId: Int
        val packetEncoder: PacketEncoder<Packet>
        try {
            packetId = packetMap.packetKey(msg.javaClass)
            packetEncoder = packetMap.encoder(packetId)
        } catch (e: ProtocolException) {
            logger.warn(e.message)
            return
        }
        buf.writeVarInt(packetId)
        logger.debug("协议包编码:packetID=$packetId $msg")
        out.add(packetEncoder.encode(buf, msg))
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val packetMap = protocol.packetMap(decodeDirection, ctx.protocolState())
        // 读取协议包id
        val packetId: Int = msg.readVarInt()
        val packetDecoder: PacketDecoder<Packet>
        try {
            // 查询协议包解码
            packetDecoder = packetMap.decoder(packetId)
        } catch (e: ProtocolException) {
            logger.trace(e.message)
            return
        }
        // 解码数据交给下一步处理
        val packet = packetDecoder.decoder(msg)
        out.add(packet)
        if (packet !is CustomPayloadPacket) logger.debug("协议包解码:packetID=$packetId $packet")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("解析包失败", cause)
        ctx.close()
    }
}


//class ProtocolPacketEncoder(
//    private val protocol: IPacketRegistry,
//    private val direction: PacketDirection
//) : MessageToMessageEncoder<Packet>() {
//    private val logger: Logger = LoggerFactory.getLogger(ProtocolPacketEncoder::class.java)
//    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
//        val packetMap = protocol.packetMap(direction, ctx.protocolState())
//        val buf = ctx.alloc().buffer()
//        val packetId = packetMap.packetKey(msg.javaClass)
//        val packetEncoder = packetMap.encoder<Packet>(packetId)
//        buf.writeVarInt(packetId)
//        logger.debug("协议包编码:packetID=$packetId $msg")
//        out.add(packetEncoder.encode(buf, msg))
//    }
//
//    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
//        logger.trace(cause.message)
//        ctx.close()
//    }
//}
//
//class ProtocolPacketDecoder(
//    private val protocol: IPacketRegistry,
//    private val direction: PacketDirection
//) : MessageToMessageDecoder<ByteBuf>() {
//    private val logger: Logger = LoggerFactory.getLogger(ProtocolPacketDecoder::class.java)
//    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
//        val packetMap = protocol.packetMap(direction, ctx.protocolState())
//        // 读取协议包id
//        val packetId: Int = msg.readVarInt()
//        // 查询协议包解码
//        val packetDecoder = packetMap.decoder<Packet>(packetId)
//        // 解码数据交给下一步处理
//        val packet = packetDecoder.decoder(msg)
//        out.add(packet)
//        logger.debug("协议包解码:packetID=$packetId $packet")
//    }
//
//    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
//        logger.trace(cause.message)
//        ctx.close()
//    }
//
//}
