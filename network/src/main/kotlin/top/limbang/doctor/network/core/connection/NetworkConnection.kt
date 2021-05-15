package top.limbang.doctor.network.core.connection

import io.netty.channel.Channel
import io.netty.channel.ChannelException
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelOutboundHandlerAdapter
import top.limbang.doctor.network.api.AbstractConnection
import top.limbang.doctor.network.core.codec.CompressionCodec
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.suspendRun
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.minecraft.netty.handler.EncryptionCodec
import javax.crypto.SecretKey

/**
 * ### 网络连接
 *
 * @see AbstractConnection
 */
class NetworkConnection(
    private val channel: Channel,
    host: String,
    port: Int,
) : AbstractConnection(host, port) {

    init {
        channel.attr(Attributes.ATTR_PROTOCOL_STATE).set(ProtocolState.HANDSHAKE)
        channel.attr(Attributes.ATTR_CONNECTION).set(this)
    }

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


    override suspend fun sendPacket(packet: Packet) {
        if (!isClosed()) {
            channel.writeAndFlush(packet).suspendRun()
        } else throw ChannelException("channel 已关闭.")

    }

    override suspend fun close(packet: Packet?) {
        if (!isClosed()) {
            if (packet != null && channel.isActive) {
                channel.writeAndFlush(packet)
                    .addListener(ChannelFutureListener.CLOSE)
                    .suspendRun()
            } else {
                channel.flush()
                channel.close().suspendRun()
            }
        } else throw ChannelException("channel 已关闭.")
    }

    override suspend fun close() {
        close(null)
    }

    override fun isClosed(): Boolean {
        return !channel.isOpen
    }
}