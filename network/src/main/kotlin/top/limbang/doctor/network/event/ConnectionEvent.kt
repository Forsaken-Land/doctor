package top.limbang.doctor.network.event

import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.core.api.event.Event

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
