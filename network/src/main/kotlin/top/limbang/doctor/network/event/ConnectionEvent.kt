package top.limbang.doctor.network.event

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.protocol.api.ProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
enum class ConnectionEvent : Event<ConnectionEventArgs> {
    Connected,
    Disconnect,
    Read,
    Error
}


data class ConnectionEventArgs(
    val context: ChannelHandlerContext? = null,
    val message: Any? = null,
    val error: Throwable? = null
)

enum class NetLifeCycleEvent: Event<NetworkManager> {
    BeforeConnect,
    BeforeShutdown
}


object ProtocolStateChange : Event<ProtocolStateChangeEventArgs>
data class ProtocolStateChangeEventArgs(
    val channel: Channel,
    val from: ProtocolState,
    val to: ProtocolState
)

