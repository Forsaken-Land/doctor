package top.fanua.doctor.network.core.connection

import io.netty.channel.Channel
import io.netty.channel.ChannelException
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.util.concurrent.Future
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.core.api.plugin.invokeMutableHook
import top.fanua.doctor.network.api.AbstractConnection
import top.fanua.doctor.network.core.codec.CompressionCodec
import top.fanua.doctor.network.core.codec.EncryptionCodec
import top.fanua.doctor.network.hooks.BeforePacketSendHook
import top.fanua.doctor.network.lib.Attributes
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.ProtocolState
import javax.crypto.SecretKey

/**
 * ### 网络连接
 *
 * @see AbstractConnection
 */
class NetworkConnection(
    private val channel: Channel,
    private val pluginManager: IPluginManager,
    override val emitter: EventEmitter,
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


    override fun sendPacket(packet: Packet): Future<*> {
        return if (!isClosed()) {
            val packetToSend = pluginManager.invokeMutableHook(BeforePacketSendHook, packet, false)

            channel.writeAndFlush(packetToSend)
        } else throw ChannelException("channel 已关闭.")

    }

    override fun close(packet: Packet?): Future<*> {
        return if (!isClosed()) {
            if (packet != null && channel.isActive) {
                channel.writeAndFlush(packet)
                    .addListener(ChannelFutureListener.CLOSE)
            } else {
                channel.flush()
                channel.close()
            }
        } else throw ChannelException("channel 已关闭.")
    }

    override fun close(): Future<*> {
        return close(null)
    }

    override fun isClosed(): Boolean {
        return !channel.isOpen
    }
}
