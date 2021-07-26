package top.limbang.doctor.plugin.extendedcrafting.protocol

import top.limbang.doctor.plugin.extendedcrafting.definations.AcknowledgeMessageDecoder
import top.limbang.doctor.plugin.extendedcrafting.definations.AcknowledgeMessageEncoder
import top.limbang.doctor.plugin.extendedcrafting.definations.SyncSingularitiesMessageDecoder
import top.limbang.doctor.plugin.forge.registry.ChannelAndId
import top.limbang.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:46
 */
object ExtendedCrafting : ICommonPacketGroup<IFML2PacketRegistry> {
    override fun registerPackets(registry: IFML2PacketRegistry) {
        registry.fml2PacketMap(PacketDirection.S2C) {
            register(ChannelAndId("extendedcrafting:main", 5), SyncSingularitiesMessageDecoder())
            register(ChannelAndId("extendedcrafting:main", 6), AcknowledgeMessageDecoder())
        }
        registry.fml2PacketMap(PacketDirection.C2S) {
            register(ChannelAndId("extendedcrafting:main", 6), AcknowledgeMessageEncoder())

        }
    }

}
