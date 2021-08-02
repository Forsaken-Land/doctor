package top.fanua.doctor.plugin.forge.registry

import top.fanua.doctor.plugin.forge.api.ModPacket
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.GroupRegistrable
import top.fanua.doctor.protocol.registry.IPacketMap

/**
 * 模组包
 */
interface IModPacketRegistry : GroupRegistrable<IModPacketRegistry> {
    fun modPacketMap(channel: String, dir: PacketDirection): IModPacketMap
    fun modPacketMap(
        channel: String,
        dir: PacketDirection,
        action: IModPacketMap.() -> Unit
    ) =
        modPacketMap(channel, dir).run(action)

    fun modPacketMap(channel: String, action: ChannelAction.() -> Unit) =
        ChannelAction(this, channel).run(action)
}
typealias IModPacketMap = IPacketMap<Int, ModPacket>
