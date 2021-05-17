package top.limbang.doctor.plugin.forge.protocol

import top.limbang.doctor.plugin.forge.api.ForgeProtocol
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.definations.fml.*
import top.limbang.doctor.plugin.forge.registry.ChannelPacketRegistryImpl
import top.limbang.doctor.plugin.forge.registry.IChannelPacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class FML1 :
    ForgeProtocol,
    IChannelPacketRegistry by
    ChannelPacketRegistryImpl(
        listOf("REGISTER", "FML|HS", "FORGE")
    ) {

    init {
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.REGISTER)
            .register("REGISTER", RegisterDecoder())
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.HELLO)
            .register("FML|HS", HelloServerDecoder())
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.MODLIST)
            .register("FML|HS", ModListDecoder())
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.REGISTERDATA)
            .register("FML|HS", RegistryDataDecoder())
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.HANDSHAKE)
            .register("FML|HS", HandshakeAckDecoder())
        channelPacketMap(PacketDirection.S2C, ForgeProtocolState.HANDSHAKE)
            .register("FORGE", ForgeChannelDecoder())

        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.REGISTER)
            .register("REGISTER", RegisterEncoder())
        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.HELLO)
            .register("FML|HS", HelloClientEncoder())
        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.MODLIST)
            .register("FML|HS", ModListEncoder())
        channelPacketMap(PacketDirection.C2S, ForgeProtocolState.HANDSHAKE)
            .register("FML|HS", HandshakeAckEncoder())

    }
}