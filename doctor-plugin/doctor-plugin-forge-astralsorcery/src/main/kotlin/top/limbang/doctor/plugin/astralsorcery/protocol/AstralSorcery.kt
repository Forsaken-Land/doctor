package top.limbang.doctor.plugin.astralsorcery.protocol

import top.limbang.doctor.plugin.astralsorcery.definations.*
import top.limbang.doctor.plugin.forge.registry.ChannelAndId
import top.limbang.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:8:31
 */
object AstralSorcery : ICommonPacketGroup<IFML2PacketRegistry> {
    override fun registerPackets(registry: IFML2PacketRegistry) {
        registry.fml2PacketMap(PacketDirection.S2C) {
            register(ChannelAndId("astralsorcery:net_channel", 0), PktLoginSyncDataHolderDecoder())
            register(ChannelAndId("astralsorcery:net_channel", 1), PktLoginSyncGatewayDecoder())
            register(ChannelAndId("astralsorcery:net_channel", 2), PktLoginSyncPerkInformationDecoder())
            register(ChannelAndId("astralsorcery:net_channel", 3), PktLoginAcknowledgeDecoder())
        }
        registry.fml2PacketMap(PacketDirection.C2S) {
            register(ChannelAndId("astralsorcery:net_channel", 3), PktLoginAcknowledgeEncoder())

        }
    }

}
