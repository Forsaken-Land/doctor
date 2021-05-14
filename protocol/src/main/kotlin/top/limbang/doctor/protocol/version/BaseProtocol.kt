package top.limbang.doctor.protocol.version

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.plugin.PluginManager

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
open class BaseProtocol(
    val emitter: EventEmitter,
    val pluginManager: PluginManager
) {
}