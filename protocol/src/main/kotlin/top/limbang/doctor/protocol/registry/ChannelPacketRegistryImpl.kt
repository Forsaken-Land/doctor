package top.limbang.doctor.protocol.registry


import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.utils.getOrCreate
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/3 下午4:06
 */
class ChannelPacketRegistryImpl : IChannelPacketRegistry {

    private val packetMap: MutableMap<PacketDirection, ChannelPacketMap> = EnumMap(PacketDirection::class.java)

    override fun packetMap(dir: PacketDirection): IChannelPacketMap {
        return packetMap.getOrCreate(dir) { ChannelPacketMap() }
    }


}

class ChannelPacketMap : IChannelPacketMap by PacketMapImpl()
