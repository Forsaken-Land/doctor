package top.limbang.doctor.protocol.hook


import top.limbang.doctor.core.api.plugin.HookMessage
import top.limbang.doctor.core.api.plugin.IPluginHookProvider
import top.limbang.doctor.protocol.registry.IPacketRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
object PacketRegistryHook : IPluginHookProvider<HookMessage<IPacketRegistry>>