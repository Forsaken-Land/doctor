package top.limbang.doctor.network.connection

import io.netty.channel.Channel
import io.netty.channel.ChannelException
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelOutboundHandlerAdapter
import top.limbang.doctor.network.codec.CompressionCodec
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.minecraft.netty.handler.EncryptionCodec
import java.util.concurrent.Future
import javax.crypto.SecretKey

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/14:20:22
 */
class NetworkConnection(
    private val channel: Channel,
    host: String,
    port: Int,
    protocolState: ProtocolState
) : AbstractConnection(
    host, port, protocolState
) {
    override fun setCompressionEnabled(threshold: Int) {
        if (isClosed()) return
        super.setCompressionEnabled(threshold)
        channel.pipeline().replace(
            "compressionCoder", "compressionCoder",
            if (isCompressionEnabled()) CompressionCodec(threshold) else ChannelOutboundHandlerAdapter()
        )
    }

    override fun setEncryptionEnabled(sharedSecret: SecretKey) {
        if (isClosed()) return
        super.setEncryptionEnabled(sharedSecret)
        channel.pipeline().replace(
            "encryptionCoder", "encryptionCoder",
            if (isEncryptionEnabled()) EncryptionCodec(sharedSecret) else ChannelOutboundHandlerAdapter()
        )
    }


    override fun sendPacket(packet: Packet): Future<Void> {
        return if (!isClosed()) {
            channel.writeAndFlush(packet)
        } else throw ChannelException("channel 已关闭.")
    }

    override fun close(packet: Packet?): Future<Void> {
        return if (!isClosed()) {
            if (packet != null && channel.isActive) {
                channel.writeAndFlush(packet).addListener(ChannelFutureListener.CLOSE)
            } else {
                channel.flush()
                channel.close()
            }
        } else throw ChannelException("channel 已关闭.")
    }

    override fun close(): Future<Void> {
        return close(null)
    }

    override fun isClosed(): Boolean {
        return !channel.isOpen
    }
}