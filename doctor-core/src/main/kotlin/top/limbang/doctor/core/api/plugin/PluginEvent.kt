package top.limbang.doctor.core.api.plugin

import top.limbang.doctor.core.api.event.Event

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
enum class PluginEvent : Event<PluginEventArgs> {
    BeforeCreate,
    Created,
    BeforeEnable,
    Enabled,
    Destroyed,
}

data class PluginEventArgs(val manager: IPluginManager, val plugin: Plugin)