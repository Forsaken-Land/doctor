package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.api.ChannelPacket
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.GroupRegistrable
import top.limbang.doctor.protocol.registry.IPacketMap

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IFML1PacketRegistry : GroupRegistrable<IFML1PacketRegistry> {
    /**
     * Channel包
     */
    fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState): IFML1PacketMap
    fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState, action: IFML1PacketMap.() -> Unit) =
        channelPacketMap(dir, state).run(action)

    fun channelPacketMap(state: ForgeProtocolState, action: DirectionActionFML1.() -> Unit) =
        DirectionActionFML1(this, state).run(action)

    var channels: List<String>
}


typealias IFML1PacketMap = IPacketMap<String, ChannelPacket>

class DirectionActionFML1(private val registry: IFML1PacketRegistry, private val state: ForgeProtocolState) {
    fun whenC2S(action: IFML1PacketMap.() -> Unit) {
        action(registry.channelPacketMap(PacketDirection.C2S, state))
    }

    fun whenS2C(action: IFML1PacketMap.() -> Unit) {
        action(registry.channelPacketMap(PacketDirection.S2C, state))
    }
}
