package top.limbang.doctor.protocol.hook

import top.limbang.doctor.core.api.plugin.Hook
import top.limbang.doctor.core.api.plugin.PluginHookProvider
import top.limbang.doctor.core.api.registry.Registry
import top.limbang.doctor.core.impl.plugin.DefaultHookProvider
import top.limbang.doctor.protocol.api.plugin.ChannelPacket
import top.limbang.doctor.protocol.registry.IPacketRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

class PacketRegistryHook : DefaultHookProvider<IPacketRegistry>()