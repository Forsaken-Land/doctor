package top.fanua.doctor.plugin.extendedcrafting.protocol

import top.fanua.doctor.plugin.extendedcrafting.definations.AcknowledgeMessageDecoder
import top.fanua.doctor.plugin.extendedcrafting.definations.AcknowledgeMessageEncoder
import top.fanua.doctor.plugin.extendedcrafting.definations.SyncSingularitiesMessageDecoder
import top.fanua.doctor.plugin.forge.registry.ChannelAndId
import top.fanua.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.ICommonPacketGroup

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
