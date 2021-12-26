package top.fanua.doctor.plugin.silentgems.protocol

import top.fanua.doctor.plugin.forge.registry.ChannelAndId
import top.fanua.doctor.plugin.forge.registry.IFML2PacketRegistry
import top.fanua.doctor.plugin.silentgems.definations.LoginPacketEncoder
import top.fanua.doctor.plugin.silentgems.definations.SyncChaosBuffsPacketDecoder
import top.fanua.doctor.plugin.silentgems.definations.SyncSoulsPacketDecoder
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/26 21:01
 */
object SilentGems : ICommonPacketGroup<IFML2PacketRegistry> {
    override fun registerPackets(registry: IFML2PacketRegistry) {
        registry.fml2PacketMap(PacketDirection.S2C) {
            register(ChannelAndId("silentgems:network", 2), SyncSoulsPacketDecoder())
            register(ChannelAndId("silentgems:network", 3), SyncChaosBuffsPacketDecoder())
        }
        registry.fml2PacketMap(PacketDirection.C2S) {
            register(ChannelAndId("silentgems:network", 5), LoginPacketEncoder())
        }
    }
}