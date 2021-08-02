package top.fanua.doctor.plugin.forge.event

import top.fanua.doctor.core.api.event.Event
import top.fanua.doctor.plugin.forge.api.ForgeProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
object ForgeStateChange : Event<ForgeStateChangeEventArgs>
data class ForgeStateChangeEventArgs(
    val from: ForgeProtocolState,
    val to: ForgeProtocolState
)

