package top.limbang.doctor.network.core

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelInboundHandler
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.socket.SocketChannel
import top.limbang.doctor.network.api.CodecInitializer
import top.limbang.doctor.network.core.codec.ProtocolPacketDecoder
import top.limbang.doctor.network.core.codec.ProtocolPacketEncoder
import top.limbang.doctor.network.core.codec.VarIntLengthBasedFrameCodec
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class DefaultClientCodecInitializer : CodecInitializer {

    override fun initChannel(ch: SocketChannel, manager: NetworkManager) {
        // 协议包处理流程
        ch.pipeline().addLast("encryptionCoder", ChannelInboundHandlerAdapter())
        ch.pipeline().addLast("varIntLengthBasedFrameCoder", VarIntLengthBasedFrameCodec())
        ch.pipeline().addLast("compressionCoder", ChannelInboundHandlerAdapter())
        ch.pipeline().addLast(
            "protocolPacketDecoder",
            ProtocolPacketDecoder(manager.protocol, PacketDirection.S2C)
        )
        ch.pipeline().addLast(
            "protocolPacketEncoder",
            ProtocolPacketEncoder(manager.protocol, PacketDirection.C2S)
        )
    }
}