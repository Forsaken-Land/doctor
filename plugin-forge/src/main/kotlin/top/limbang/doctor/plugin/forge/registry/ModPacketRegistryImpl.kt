package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.PacketMapImpl
import top.limbang.doctor.protocol.utils.getOrCreate
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
