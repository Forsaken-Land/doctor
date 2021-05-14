package top.limbang.doctor.core.api.plugin

import top.limbang.doctor.core.api.event.Event

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class PluginEvent(val name: String) : Event<PluginEventArgs> {

}

open class PluginEventArgs(val sender: String)