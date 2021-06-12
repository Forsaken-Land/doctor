package top.limbang.doctor.network.core.connection

import io.netty.channel.Channel
import io.netty.channel.ChannelException
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.util.concurrent.Future
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.network.api.AbstractConnection
import top.limbang.doctor.network.core.codec.CompressionCodec
import top.limbang.doctor.network.core.codec.EncryptionCodec
import top.limbang.doctor.network.hooks.BeforePacketSendHook
import top.limbang.doctor.network.hooks.BeforePacketSendHookOperation
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
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
            val hook = BeforePacketSendHookOperation(false, packet)
            pluginManager.invokeHook(BeforePacketSendHook::class.java, hook, false)
            if (hook.modified) {
                channel.writeAndFlush(hook.packet)
            } else {
                channel.writeAndFlush(packet)
            }
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