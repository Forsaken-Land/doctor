package top.limbang.doctor.plugin.forge.protocol

import top.limbang.doctor.plugin.forge.api.ForgeProtocol
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.definations.fml2.*
import top.limbang.doctor.plugin.forge.registry.ChannelPacketRegistryImpl
import top.limbang.doctor.plugin.forge.registry.IChannelPacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/16 下午9:51
 */
class FML2 :
    ForgeProtocol,
    IChannelPacketRegistry by
    ChannelPacketRegistryImpl(
        listOf("REGISTER", "FML|HS", "FORGE")
    ) {
    init {
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.REGISTER).register(
            "fml:loginwrapper", LoginWrapperDecoder()
        )
        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.REGISTER).register(
            "fml:loginwrapper", LoginWrapperEncoder()
        )
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.REGISTER).register(
            "1", ModListDecoder()
        )
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.REGISTER).register(
            "3", ServerRegisterDecoder()
        )
        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.REGISTER).register(
            "2", ModListEncoder()
        )
        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.REGISTER).register(
            "99", AcknowledgementEncoder()
        )
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.REGISTER).register(
            "4", ConfigurationDataDecoder()
        )
    }
}
