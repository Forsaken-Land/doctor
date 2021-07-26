package top.limbang.doctor.plugin.silentgear.protocol

import top.limbang.doctor.plugin.forge.registry.ChannelAndId
import top.limbang.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.limbang.doctor.plugin.silentgear.definations.LoginPacketEncoder
import top.limbang.doctor.plugin.silentgear.definations.SyncGearPartsPacketDecoder
import top.limbang.doctor.plugin.silentgear.definations.SyncMaterialsPacketDecoder
import top.limbang.doctor.plugin.silentgear.definations.SyncTraitsPacketDecoder
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:15
 */
object SilentGear : ICommonPacketGroup<IFML2PacketRegistry> {
    override fun registerPackets(registry: IFML2PacketRegistry) {
        registry.fml2PacketMap(PacketDirection.S2C) {
            register(ChannelAndId("silentgear:network", 1), SyncTraitsPacketDecoder())
            register(ChannelAndId("silentgear:network", 2), SyncGearPartsPacketDecoder())
            register(ChannelAndId("silentgear:network", 6), SyncMaterialsPacketDecoder())
        }
        registry.fml2PacketMap(PacketDirection.C2S) {
            register(ChannelAndId("silentgear:network", 3), LoginPacketEncoder())
        }
    }
}
