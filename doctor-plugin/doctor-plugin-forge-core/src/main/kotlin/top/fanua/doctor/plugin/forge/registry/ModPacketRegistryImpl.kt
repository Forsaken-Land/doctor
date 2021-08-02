package top.fanua.doctor.plugin.forge.registry

import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.PacketMapImpl
import top.fanua.doctor.protocol.utils.getOrCreate
import java.util.*

class ModPacketRegistryImpl : IModPacketRegistry {
    private val modPacketMap: MutableMap<String, MutableMap<PacketDirection, IModPacketMap>> =
        HashMap()

    override fun modPacketMap(channel: String, dir: PacketDirection): IModPacketMap {
        return modPacketMap.getOrCreate(channel) {
            EnumMap(PacketDirection::class.java)
        }.getOrCreate(dir) {
            DefaultModPacketMap()
        }
    }
}

class DefaultModPacketMap : IModPacketMap by PacketMapImpl()
