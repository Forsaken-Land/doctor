package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.api.ModPacket
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.GroupRegistrable
import top.limbang.doctor.protocol.registry.IPacketMap

/**
 * 模组包
 */
interface IModPacketRegistry : GroupRegistrable<IModPacketRegistry> {
    fun modPacketMap(channel: String, dir: PacketDirection, state: ForgeProtocolState): IModPacketMap
    fun modPacketMap(
        channel: String,
        dir: PacketDirection,
        state: ForgeProtocolState,
        action: IModPacketMap.() -> Unit
    ) =
        modPacketMap(channel, dir, state).run(action)

    fun modPacketMap(channel: String, action: ChannelAction.() -> Unit) =
        ChannelAction(this, channel).run(action)
}typealias IModPacketMap = IPacketMap<Int, ModPacket>