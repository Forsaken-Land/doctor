package top.limbang.doctor.network.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.extension.writeVarInt

/**
 * ### 基于”VarInt“长度的编解码器
 *
 */
class VarIntLengthBasedFrameCodec : ByteToMessageCodec<ByteBuf>() {
    private val logger: Logger = LoggerFactory.getLogger(VarIntLengthBasedFrameCodec::class.java)

    override fun encode(ctx: ChannelHandlerContext?, msg: ByteBuf, out: ByteBuf) {
        out.writeVarInt(msg.readableBytes())
        out.writeBytes(msg)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        msg.markReaderIndex()
        if (!msg.isReadable) {
            return
        }
        val remainingPacketLength: Int = msg.readVarInt()
        // 判断可读字节是否小于要读数据包长度
        if (msg.readableBytes() < remainingPacketLength) {
            // 如果小于说明数据还没接收完,重新定位到之前标记的读取索引,等待后面的数据
            msg.resetReaderIndex()
            return
        }
        val newBuf = ctx.alloc().buffer(remainingPacketLength)
        msg.readBytes(newBuf, remainingPacketLength)
        out.add(newBuf)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("编解码数据长度时发生错误", cause)
        ctx.close()
    }


}

