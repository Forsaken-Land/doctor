package top.fanua.doctor.network.core.codec

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.MessageToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.ShortBufferException
import javax.crypto.spec.IvParameterSpec

/**
 * ### 4.加密编码
 */
class EncryptionCodec(sharedSecret: SecretKey) : MessageToMessageCodec<ByteBuf, ByteBuf>() {
    private val logger: Logger = LoggerFactory.getLogger(EncryptionCodec::class.java)

    private val cipherDecrypt = Cipher.getInstance("AES/CFB8/NoPadding")
    private val cipherEncrypt = Cipher.getInstance("AES/CFB8/NoPadding")

    init {
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, sharedSecret, IvParameterSpec(sharedSecret.encoded))
        cipherDecrypt.init(Cipher.DECRYPT_MODE, sharedSecret, IvParameterSpec(sharedSecret.encoded))
    }

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        out.add(encrypt(msg))
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        out.add(decrypt(msg))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("数据加密时发生错误", cause)
        ctx.close()
    }

    /**
     * ### 加密
     *
     * @param data 数据
     */
    private fun encrypt(data: ByteBuf): ByteBuf {
        return try {
            update(data, false)
        } catch (e: ShortBufferException) {
            throw DecoderException("加密输出缓冲区太小", e)
        }
    }

    /**
     * ### 解密
     *
     * @param data 数据
     */
    private fun decrypt(data: ByteBuf): ByteBuf {
        return try {
            update(data, true)
        } catch (e: ShortBufferException) {
            throw DecoderException("解密输出缓冲区太小", e)
        }

    }

    /**
     * ### 更新数据
     *
     * @param data 数据
     * @param decrypt 是否是解密
     */
    private fun update(data: ByteBuf, decrypt: Boolean): ByteBuf {
        val outNioBuf = ByteBuffer.allocate(data.readableBytes())
        if (decrypt) {
            cipherDecrypt.update(data.nioBuffer(), outNioBuf)
        } else {
            cipherEncrypt.update(data.nioBuffer(), outNioBuf)
        }
        outNioBuf.flip()
        return Unpooled.wrappedBuffer(outNioBuf)
    }


}
