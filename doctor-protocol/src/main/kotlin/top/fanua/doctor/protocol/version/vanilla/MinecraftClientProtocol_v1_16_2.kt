package top.fanua.doctor.protocol.version.vanilla

import top.fanua.doctor.core.api.plugin.HookMessage
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.definition.login.client.EncryptionResponseEncoder
import top.fanua.doctor.protocol.definition.login.client.LoginPluginResponseEncoder
import top.fanua.doctor.protocol.definition.login.client.LoginStartEncoder
import top.fanua.doctor.protocol.definition.login.server.*
import top.fanua.doctor.protocol.definition.login.server.DisconnectDecoder
import top.fanua.doctor.protocol.definition.play.client.*
import top.fanua.doctor.protocol.definition.play.server.CChatEncoder
import top.fanua.doctor.protocol.definition.play.server.CTabCompleteType1Encoder
import top.fanua.doctor.protocol.hook.PacketRegistryHook
import top.fanua.doctor.protocol.registry.IPacketRegistry
import top.fanua.doctor.protocol.registry.PacketRegistryImpl
import top.fanua.doctor.protocol.version.CommonProtocol


/**
 * ### Minecraft 客户端协议
 *
 * 版本 1.16.2
 */
class MinecraftClientProtocol_v1_16_2(pluginManager: IPluginManager) : IPacketRegistry by PacketRegistryImpl() {
    init {

        registerGroup(CommonProtocol)
        packetMap(ProtocolState.LOGIN) {
            whenS2C {
                register(0x00, DisconnectDecoder())
                register(0x01, EncryptionRequestDecoder())
                register(0x02, LoginSuccessAfter340Decoder())
                register(0x03, SetCompressionDecoder())
                register(0x04, LoginPluginRequestDecoder())
            }
            whenC2S {
                register(0x00, LoginStartEncoder())
                register(0x01, EncryptionResponseEncoder())
                register(0x02, LoginPluginResponseEncoder())
            }
        }
        packetMap(ProtocolState.PLAY) {
            whenS2C {
                register(0x1F, KeepAliveDecoder())
                register(0x24, JoinGameType1Decoder())
                register(0x17, CustomPayloadDecoder())
                register(0x20, ChunkDataType1Decoder())
                register(0x0D, ServerDifficultyType1Decoder())
                register(0x34, PlayerPositionAndLookDecoder())
                register(0x0E, ChatType1Decoder())
                register(0x0F, STabCompleteType1Decoder())
                register(0x31, CombatEventDecoder())
                register(0x32, PlayerListItemDecoder())

            }
            whenC2S {
                register(0x10, KeepAliveEncoder())
                register(0x05, ClientSettingEncoder())
                register(0x00, TeleportConfirmEncoder())
                register(0x04, ClientStatusEncoder())
                register(0x03, CChatEncoder())
                register(0x06, CTabCompleteType1Encoder())
            }
        }
        pluginManager.invokeHook(PacketRegistryHook, HookMessage(this), true)
    }
}
