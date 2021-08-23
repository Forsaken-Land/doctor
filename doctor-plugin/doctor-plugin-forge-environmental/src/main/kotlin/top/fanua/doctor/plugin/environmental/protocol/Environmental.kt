package top.fanua.doctor.plugin.environmental.protocol

import top.fanua.doctor.plugin.environmental.definations.AcknowledgeEnvironmentalMessageEncoder
import top.fanua.doctor.plugin.environmental.definations.SyncBackpackTypeMessageDecoder
import top.fanua.doctor.plugin.environmental.definations.SyncSlabfishTypeMessageDecoder
import top.fanua.doctor.plugin.environmental.definations.SyncSweaterTypeMessageDecoder
import top.fanua.doctor.plugin.forge.registry.ChannelAndId
import top.fanua.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/24:0:40
 */
object Environmental : ICommonPacketGroup<IFML2PacketRegistry> {
    override fun registerPackets(registry: IFML2PacketRegistry) {
        registry.fml2PacketMap(PacketDirection.S2C) {
            register(ChannelAndId("environmental:login", 0), SyncBackpackTypeMessageDecoder())
            register(ChannelAndId("environmental:login", 1), SyncSlabfishTypeMessageDecoder())
            register(ChannelAndId("environmental:login", 2), SyncSweaterTypeMessageDecoder())
        }
        registry.fml2PacketMap(PacketDirection.C2S) {
            register(ChannelAndId("environmental:login", 99), AcknowledgeEnvironmentalMessageEncoder())
        }
    }
}
