package top.fanua.doctor.plugin.forge.protocol

import top.fanua.doctor.plugin.forge.definations.fml2.*
import top.fanua.doctor.plugin.forge.registry.ChannelAndId
import top.fanua.doctor.plugin.forge.registry.FML2PacketRegistryImpl
import top.fanua.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.fanua.doctor.protocol.core.PacketDirection

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/16 下午9:51
 */
class FML2 :
    IFML2PacketRegistry by
    FML2PacketRegistryImpl() {
    init {
        fml2PacketMap(PacketDirection.S2C)
            .register(
                ChannelAndId("fml:handshake", 1), ModListDecoder()
            ).register(
                ChannelAndId("fml:handshake", 3), ServerRegisterDecoder()
            ).register(
                ChannelAndId("fml:handshake", 4), ConfigurationDataDecoder()
            )
        fml2PacketMap(PacketDirection.C2S)
            .register(
                ChannelAndId("fml:handshake", 2), ModListEncoder()
            ).register(
                ChannelAndId("fml:handshake", 99), AcknowledgementEncoder()
            )
    }
}
