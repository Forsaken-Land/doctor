package top.fanua.doctor.network.lib

import io.netty.util.AttributeKey
import top.fanua.doctor.network.api.Connection
import top.fanua.doctor.protocol.api.ProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
object Attributes {
    val ATTR_CONNECTION = AttributeKey.valueOf<Connection>("connection")
    val ATTR_PROTOCOL_STATE = AttributeKey.valueOf<ProtocolState>("protocol_state")
}
