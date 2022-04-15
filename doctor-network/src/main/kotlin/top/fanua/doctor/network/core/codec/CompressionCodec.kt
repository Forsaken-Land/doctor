package top.fanua.doctor.network.core.codec

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.protocol.extension.readVarInt
import top.fanua.doctor.protocol.extension.writeVarInt
import java.util.zip.Deflater
import java.util.zip.Inflater

/**
 * ### 压缩编解码
 *
 * 如果要压缩数据的长度小于服务器压缩阈值[threshold]则不压缩,直接发送长度为0的VarInt加数据。
 */
class CompressionCodec(private val threshold: Int) : MessageToMessageCodec<ByteBuf, ByteBuf>() {
    private val logger: Logger = LoggerFactory.getLogger(CompressionCodec::class.java)
    private val inflater = Inflater()
    private val deflate = Deflater()
    private val buffer = ByteArray(8192)

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val dataLength = msg.readableBytes()
        val buf = ctx.alloc().buffer()
        if (dataLength < threshold) {
            buf.writeVarInt(0)
            buf.writeBytes(msg)
            out.add(buf)
        } else {
            val input = ByteArray(dataLength)
            msg.readBytes(input)
            buf.writeVarInt(input.size)
            deflate.setInput(input, 0, dataLength)
            deflate.finish()
            while (!deflate.finished()) {
                val length = deflate.deflate(buffer)
                buf.writeBytes(buffer, 0, length)
            }
            out.add(buf)
            deflate.reset()
        }
    }

    override fun decode(ctx: ChannelHandlerContext, mgs: ByteBuf, out: MutableList<Any>) {
        if (mgs.readableBytes() <= 0) return
        val dataLength = mgs.readVarInt()
        if (dataLength == 0) {
            out.add(mgs.readBytes(mgs.readableBytes()))
            return
        }

        if (dataLength < threshold) throw DecoderException("数据长度:$dataLength 小于服务器压缩阈值:$threshold")
        if (dataLength > 2097152) logger.warn("数据长度:$dataLength 超过协议规定最大值:2097152")
        if (dataLength > 2097152 * 3) logger.error("数据长度:$dataLength 超过 [3] 倍协议规定最大值:2097152")
        if (dataLength > 2097152 * 6) throw DecoderException("数据长度:$dataLength 超过 [6] 倍协议规定最大值:2097152")

        val input = ByteArray(mgs.readableBytes())
        mgs.readBytes(input)
        inflater.setInput(input)
        val output = ByteArray(dataLength)
        inflater.inflate(output)
        out.add(Unpooled.wrappedBuffer(output))
        inflater.reset()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("数据压缩或解压时发生错误", cause)
        ctx.close()
    }

}
