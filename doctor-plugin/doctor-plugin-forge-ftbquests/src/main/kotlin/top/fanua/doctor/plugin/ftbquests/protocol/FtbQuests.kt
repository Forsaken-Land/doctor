package top.fanua.doctor.plugin.ftbquests.protocol

import top.fanua.doctor.plugin.forge.registry.IModPacketRegistry
import top.fanua.doctor.plugin.ftbquests.definations.MessageClaimAllRewardsEncoder
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.ICommonPacketGroup

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:13:36
 */
object FtbQuests : ICommonPacketGroup<IModPacketRegistry> {
    override fun registerPackets(registry: IModPacketRegistry) {
        registry.run {
            modPacketMap("ftbquests", PacketDirection.C2S) {
                register(11, MessageClaimAllRewardsEncoder())
            }
        }
    }
}
