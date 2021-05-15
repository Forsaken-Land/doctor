package top.limbang.doctor.plugin.forge.event

import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState

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