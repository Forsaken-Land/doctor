package top.limbang.doctor.client.plugin

import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.core.api.plugin.IPluginHookProvider
import top.limbang.doctor.core.api.plugin.MutableHookMessage

/**
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
object ClientAddListenerHook : IPluginHookProvider<MutableHookMessage<EventListener>>