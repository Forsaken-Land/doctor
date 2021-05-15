package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.PacketMapImpl
import top.limbang.doctor.protocol.utils.getOrCreate
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class ChannelPacketRegistryImpl : IChannelPacketRegistry {

    private val channelMap: MutableMap<PacketDirection, MutableMap<ForgeProtocolState, IChannelPacketMap>> =
        EnumMap(PacketDirection::class.java)


    override fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState): IChannelPacketMap {

        return channelMap.getOrCreate(dir) {
            EnumMap(ForgeProtocolState::class.java)
        }.getOrCreate(state) { DefaultChannelPacketMap() }
    }

}


class DefaultChannelPacketMap : IChannelPacketMap by PacketMapImpl()

