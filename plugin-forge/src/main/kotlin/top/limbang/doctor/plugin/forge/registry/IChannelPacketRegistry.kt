package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ChannelPacket
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.GroupRegistrable
import top.limbang.doctor.protocol.registry.IPacketMap

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IChannelPacketRegistry : GroupRegistrable<IChannelPacketRegistry> {
    /**
     * Channel包
     */
    fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState): IChannelPacketMap
    fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState, action: IChannelPacketMap.() -> Unit) =
        channelPacketMap(dir, state).run(action)

    fun channelPacketMap(state: ForgeProtocolState, action: DirectionActionChannel.() -> Unit) =
        DirectionActionChannel(this, state).run(action)

}


typealias IChannelPacketMap = IPacketMap<String, ChannelPacket>

class DirectionActionChannel(private val registry: IChannelPacketRegistry, private val state: ForgeProtocolState) {
    fun whenC2S(action: IChannelPacketMap.() -> Unit) {
        action(registry.channelPacketMap(PacketDirection.C2S, state))
    }

    fun whenS2C(action: IChannelPacketMap.() -> Unit) {
        action(registry.channelPacketMap(PacketDirection.S2C, state))
    }
}
