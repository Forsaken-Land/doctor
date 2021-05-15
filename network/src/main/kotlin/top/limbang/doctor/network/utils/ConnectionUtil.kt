package top.limbang.doctor.network.utils

import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.protocol.api.ProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */


fun ChannelHandlerContext.protocolState(): ProtocolState {
    val attr = this.channel().attr(Attributes.ATTR_PROTOCOL_STATE)
    if (attr.get() == null) {
        attr.set(ProtocolState.HANDSHAKE)
    }
    return attr.get()
}

fun ChannelHandlerContext.setProtocolState(state: ProtocolState) {
    return this.channel().attr(Attributes.ATTR_PROTOCOL_STATE).set(state)
}


