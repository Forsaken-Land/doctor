package top.fanua.doctor.client.plugin

import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.core.api.plugin.IPluginHookProvider
import top.fanua.doctor.core.api.plugin.MutableHookMessage

/**
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
object ClientAddListenerHook : IPluginHookProvider<MutableHookMessage<EventListener>>
