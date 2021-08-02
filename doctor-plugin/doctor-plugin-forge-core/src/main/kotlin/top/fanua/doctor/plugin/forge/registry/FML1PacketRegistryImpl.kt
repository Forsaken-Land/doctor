package top.fanua.doctor.plugin.forge.registry

import top.fanua.doctor.plugin.forge.api.ForgeProtocolState
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.PacketMapImpl
import top.fanua.doctor.protocol.utils.getOrCreate
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class FML1PacketRegistryImpl(override var channels: List<String>) : IFML1PacketRegistry {

    private val channelMap: MutableMap<PacketDirection, MutableMap<ForgeProtocolState, IFML1PacketMap>> =
        EnumMap(PacketDirection::class.java)


    override fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState): IFML1PacketMap {

        return channelMap.getOrCreate(dir) {
            EnumMap(ForgeProtocolState::class.java)
        }.getOrCreate(state) { DefaultFML1PacketMap() }
    }

}


class DefaultFML1PacketMap : IFML1PacketMap by PacketMapImpl()

