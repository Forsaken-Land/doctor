package top.limbang.doctor.network.lib

import io.netty.util.AttributeKey
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
object Attributes {
    val ATTR_CONNECTION = AttributeKey.valueOf<Connection>("connection")
    val ATTR_PROTOCOL_STATE= AttributeKey.valueOf<ProtocolState>("protocol_state")
}
