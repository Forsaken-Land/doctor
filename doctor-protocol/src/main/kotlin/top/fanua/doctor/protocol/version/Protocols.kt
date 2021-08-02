package top.fanua.doctor.protocol.version

import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.protocol.registry.ChannelPacketRegistryImpl
import top.fanua.doctor.protocol.registry.IChannelPacketRegistry
import top.fanua.doctor.protocol.registry.IPacketRegistry
import top.fanua.doctor.protocol.version.vanilla.MinecraftClientChannel_v1_12_2
import top.fanua.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_12_2
import top.fanua.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_16_2
import top.fanua.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_7_10

/**
 * ### 创建协议
 * @param versionNumber 协议版本号,参考[ProtocolVersion]
 * @param pluginManager 插件管理器
 */
fun createProtocol(versionNumber: ProtocolVersion, pluginManager: IPluginManager): IPacketRegistry {
    return when (versionNumber) {
        ProtocolVersion.V1_12_2 -> MinecraftClientProtocol_v1_12_2(pluginManager)

        ProtocolVersion.V1_16_2, ProtocolVersion.V1_16_5 -> MinecraftClientProtocol_v1_16_2(pluginManager)

        ProtocolVersion.V1_7_10 -> MinecraftClientProtocol_v1_7_10(pluginManager)

    }
}

/**
 * ### 创建插件通道
 * @param versionNumber 协议版本号,参考[ProtocolVersion]
 */
fun createChannel(versionNumber: ProtocolVersion): IChannelPacketRegistry {
    return when (versionNumber) {
        ProtocolVersion.V1_16_2 -> {
            MinecraftClientChannel_v1_12_2()
        }

        else -> {
            DefaultChannelRegistry()
        }
    }
}

class DefaultChannelRegistry : IChannelPacketRegistry by ChannelPacketRegistryImpl()
