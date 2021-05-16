package top.limbang.doctor.client.event

import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.protocol.api.ProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-16
 */

object ProtocolStateChange : Event<ProtocolStateChangeEventArgs>
data class ProtocolStateChangeEventArgs(
    val from: ProtocolState,
    val to: ProtocolState
)
