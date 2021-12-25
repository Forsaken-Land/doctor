package top.fanua.doctor.protocol.version.vanilla

import top.fanua.doctor.core.api.plugin.HookMessage
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.definition.channel.RegisterDecoder
import top.fanua.doctor.protocol.definition.channel.RegisterEncoder
import top.fanua.doctor.protocol.definition.login.client.EncryptionResponseEncoder
import top.fanua.doctor.protocol.definition.login.client.LoginPluginResponseEncoder
import top.fanua.doctor.protocol.definition.login.client.LoginStartEncoder
import top.fanua.doctor.protocol.definition.login.server.EncryptionRequestDecoder
import top.fanua.doctor.protocol.definition.login.server.LoginPluginRequestDecoder
import top.fanua.doctor.protocol.definition.login.server.LoginSuccess340Decoder
import top.fanua.doctor.protocol.definition.login.server.SetCompressionDecoder
import top.fanua.doctor.protocol.definition.play.client.*
import top.fanua.doctor.protocol.definition.play.server.CChatEncoder
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionAndLookEncoder
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionEncoder
import top.fanua.doctor.protocol.definition.play.server.CTabCompleteType0Encoder
import top.fanua.doctor.protocol.hook.PacketRegistryHook
import top.fanua.doctor.protocol.registry.ChannelPacketRegistryImpl
import top.fanua.doctor.protocol.registry.IChannelPacketRegistry
import top.fanua.doctor.protocol.registry.IPacketRegistry
import top.fanua.doctor.protocol.registry.PacketRegistryImpl
import top.fanua.doctor.protocol.version.CommonProtocol

/**
 * ### Minecraft 客户端协议
 *
 * 版本 1.12.2
 */
class MinecraftClientChannel_v1_12_2 : IChannelPacketRegistry by ChannelPacketRegistryImpl() {
    init {
        packetMap(PacketDirection.S2C)
            .register("REGISTER", RegisterDecoder())
        packetMap(PacketDirection.C2S)
            .register("REGISTER", RegisterEncoder())
    }
}

class MinecraftClientProtocol_v1_12_2(pluginManager: IPluginManager) : IPacketRegistry by PacketRegistryImpl() {

    init {

        registerGroup(CommonProtocol)
        packetMap(ProtocolState.LOGIN) {
            whenS2C {
                register(0x00, top.fanua.doctor.protocol.definition.login.server.DisconnectDecoder())
                register(0x01, EncryptionRequestDecoder())
                register(0x02, LoginSuccess340Decoder())
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
                register(0x0D, ServerDifficultyType0Decoder())
                register(0x23, JoinGameType0Decoder())
                register(0x2F, PlayerPositionAndLookDecoder())
                register(0x1D, UnloadChunkDecoder())
                register(0x1F, KeepAliveDecoder())
                register(0x2D, CombatEventDecoder())
                register(0x1A, DisconnectDecoder())
                register(0x0F, ChatType0Decoder())
                register(0x18, CustomPayloadDecoder())
                register(0x2E, PlayerListItemDecoder())
                register(0x20, ChunkDataType0Decoder())
                register(0x0E, STabCompleteType0Decoder())
                register(0x41, UpdateHealthDecoder())
                register(0x4E, EntityPropertiesDecoder())
            }
            whenC2S {
                register(0x0B, KeepAliveEncoder())
                register(0x04, ClientSettingEncoder())
                register(0x00, TeleportConfirmEncoder())
                register(0x02, CChatEncoder())
                register(0x09, CustomPayloadEncoder())
                register(0x03, ClientStatusEncoder())
                register(0x01, CTabCompleteType0Encoder())
                register(0x0A, UseEntityEncoder())
                register(0X0D, CPlayerPositionEncoder())
                register(0x0E, CPlayerPositionAndLookEncoder())
                register(0x15, EntityActionEncoder())
            }
        }
        pluginManager.invokeHook(PacketRegistryHook, HookMessage(this), true)


    }

}
