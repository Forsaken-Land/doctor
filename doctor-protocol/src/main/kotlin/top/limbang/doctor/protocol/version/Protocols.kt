package top.limbang.doctor.protocol.version

import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.protocol.core.ProtocolException
import top.limbang.doctor.protocol.registry.ChannelPacketRegistryImpl
import top.limbang.doctor.protocol.registry.IChannelPacketRegistry
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.version.vanilla.MinecraftClientChannel_v1_12_2
import top.limbang.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_12_2
import top.limbang.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_16_2

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

/**
 * 获取协议
 */
fun createProtocol(versionNumber: Int, pluginManager: IPluginManager): IPacketRegistry {
    return when (versionNumber) {
        v1_12_2 -> MinecraftClientProtocol_v1_12_2(pluginManager)

        v1_16_2, v1_16_5 -> MinecraftClientProtocol_v1_16_2(pluginManager)

        else -> {
            throw ProtocolException("找不到协议号$versionNumber")
        }
    }
}

fun createChannel(versionNumber: Int): IChannelPacketRegistry {
    return when (versionNumber) {
        v1_16_2 -> {
            MinecraftClientChannel_v1_12_2()
        }

        else -> {
            DefaultChannelRegistry()
        }
    }
}

class DefaultChannelRegistry : IChannelPacketRegistry by ChannelPacketRegistryImpl() {

}
