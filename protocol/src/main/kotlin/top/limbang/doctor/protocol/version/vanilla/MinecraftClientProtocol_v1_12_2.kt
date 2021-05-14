package top.limbang.doctor.protocol.version.vanilla

import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.play.client.*
import top.limbang.doctor.protocol.definition.play.server.ChatEncoderC
import top.limbang.doctor.protocol.hook.PacketRegistryHook
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.registry.PacketRegistryImpl
import top.limbang.doctor.protocol.version.CommonProtocol

/**
 * ### Minecraft 客户端协议
 *
 * 版本 1.12.2
 */
class MinecraftClientProtocol_v1_12_2(pluginManager: PluginManager) : IPacketRegistry by PacketRegistryImpl() {

    init {

        registerGroup(CommonProtocol)

        packetMap(ProtocolState.PLAY) {
            whenS2C {
                register(0x23, JoinGameDecoder())
                register(0x2F, PlayerPositionAndLookDecoder())
                register(0x1F, KeepAliveDecoder())
                register(0x2D, CombatEventDecoder())
                register(0x1A, DisconnectDecoder())
                register(0x0F, ChatDecoder())
            }
            whenC2S {
                register(0x0B, KeepAliveEncoder())
                register(0x04, ClientSettingEncoder())
                register(0x00, TeleportConfirmEncoder())
                register(0x02, ChatEncoderC())
            }
        }
        pluginManager.invokeHook(PacketRegistryHook::class.java, this, true)


    }

}