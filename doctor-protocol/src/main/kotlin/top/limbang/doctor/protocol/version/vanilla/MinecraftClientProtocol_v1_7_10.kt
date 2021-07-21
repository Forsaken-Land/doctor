package top.limbang.doctor.protocol.version.vanilla

import top.limbang.doctor.core.api.plugin.HookMessage
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.login.client.EncryptionResponseBeforeEncoder
import top.limbang.doctor.protocol.definition.login.client.LoginStartEncoder
import top.limbang.doctor.protocol.definition.login.server.EncryptionRequestBeforeDecoder
import top.limbang.doctor.protocol.definition.login.server.LoginSuccess340Decoder
import top.limbang.doctor.protocol.definition.play.client.*
import top.limbang.doctor.protocol.hook.PacketRegistryHook
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.registry.PacketRegistryImpl
import top.limbang.doctor.protocol.version.CommonProtocol

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/8:1:38
 */
/**
 * ### Minecraft 客户端协议
 *
 * 版本 1.16.2
 */
class MinecraftClientProtocol_v1_7_10(pluginManager: IPluginManager) : IPacketRegistry by PacketRegistryImpl() {
    init {
        registerGroup(CommonProtocol)
        packetMap(ProtocolState.LOGIN) {
            whenC2S {
                register(0x00, LoginStartEncoder())
                register(0x01, EncryptionResponseBeforeEncoder())
            }
            whenS2C {
                register(0x01, EncryptionRequestBeforeDecoder())
                register(0x02, LoginSuccess340Decoder())
            }
        }
        packetMap(ProtocolState.PLAY) {
            whenS2C {
                register(0x00, KeepAliveBeforeDecoder())
                register(0x01, JoinGameType2Decoder())
                register(0x02, ChatType0Decoder())
                register(0x1A, EntityStatusDecoder())
                register(0x3F, CustomPayloadBeforeDecoder())
                register(0x40, DisconnectDecoder())
            }
            whenC2S {
                register(0x00, KeepAliveBeforeEncoder())
                register(0x15, ClientSettingEncoder())
                register(0x16, ClientStatusEncoder())
                register(0x17, CustomPayloadBeforeEncoder())
            }
        }
        pluginManager.invokeHook(PacketRegistryHook, HookMessage(this), true)
    }
}
