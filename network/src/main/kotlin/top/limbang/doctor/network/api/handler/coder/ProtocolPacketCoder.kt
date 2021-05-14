package top.limbang.doctor.network.api.handler.coder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.network.api.handler.Connection
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.extension.writeVarInt
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.registry.PacketDirection

/**
 * ### 协议包编解码
 *
 * - [protocol]协议版本
 */
class ProtocolPacketCoder(private val protocol: IPacketRegistry) : MessageToMessageCodec<ByteBuf, Packet>() {
    private val logger: Logger = LoggerFactory.getLogger(ProtocolPacketCoder::class.java)

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        val connection = ctx.channel().attr(AttributeKey.valueOf<Connection>("connection")).get()
        val buf = ctx.alloc().buffer()
        val packetId = protocol.packetMap(PacketDirection.C2S, connection.protocolState).packetId(msg.javaClass)
        val packetEncoder = protocol.packetMap(PacketDirection.C2S, connection.protocolState).encoder<Packet>(packetId)
        buf.writeVarInt(packetId)
        logger.debug("协议包编码:packetID=$packetId $msg")
        out.add(packetEncoder.encode(buf, msg))


    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val connection = ctx.channel().attr(AttributeKey.valueOf<Connection>("connection")).get()
        // 读取协议包id
        val packetId: Int = msg.readVarInt()
        // 查询协议包解码
        val packetDecoder = protocol.packetMap(PacketDirection.S2C, connection.protocolState).decoder<Packet>(packetId)
        // 解码数据交给下一步处理
        val packet = packetDecoder.decoder(msg)
        out.add(packet)
        logger.debug("协议包解码:packetID=$packetId $packet")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.trace(cause.message)
//        logger.error(cause.message)
//        ctx.close()
    }
}