package top.fanua.doctor.protocol.hook


import top.fanua.doctor.core.api.plugin.HookMessage
import top.fanua.doctor.core.api.plugin.IPluginHookProvider
import top.fanua.doctor.protocol.registry.IPacketRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
object PacketRegistryHook : IPluginHookProvider<HookMessage<IPacketRegistry>>
