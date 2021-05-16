package top.limbang.doctor.network.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.event.ProtocolStateChange
import top.limbang.doctor.network.event.ProtocolStateChangeEventArgs
import top.limbang.doctor.network.lib.Attributes.ATTR_CONNECTION
import top.limbang.doctor.network.lib.Attributes.ATTR_PROTOCOL_STATE
import top.limbang.doctor.protocol.api.ProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */


fun ChannelHandlerContext.protocolState(): ProtocolState = this.channel().protocolState()
fun ChannelHandlerContext.connection(): Connection = this.channel().connection()

fun Channel.protocolState(): ProtocolState {
    val attr = this.attr(ATTR_PROTOCOL_STATE)
    if (attr.get() == null) {
        attr.set(ProtocolState.HANDSHAKE)
    }
    return attr.get()
}

fun Channel.connection(): Connection {
    return this.attr(ATTR_CONNECTION).get()
}

fun ChannelHandlerContext.setProtocolState(state: ProtocolState) {
    this.channel().setProtocolState(state)
}

fun Channel.setProtocolState(state: ProtocolState) {
    val old = this.attr(ATTR_PROTOCOL_STATE).getAndSet(state)
    this.connection().emitter.emit(ProtocolStateChange, ProtocolStateChangeEventArgs(this, old, state))
}


