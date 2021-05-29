package top.limbang.doctor.protocol.version

import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.protocol.core.ProtocolException
import top.limbang.doctor.protocol.registry.IPacketRegistry
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
fun createProtocol(name: String, pluginManager: IPluginManager): IPacketRegistry {
    return when (name) {
        "1.12.2" -> MinecraftClientProtocol_v1_12_2(pluginManager)

        "1.16.2" -> MinecraftClientProtocol_v1_16_2(pluginManager)

        else -> {
            throw ProtocolException("找不到协议$name")
        }
    }
}
