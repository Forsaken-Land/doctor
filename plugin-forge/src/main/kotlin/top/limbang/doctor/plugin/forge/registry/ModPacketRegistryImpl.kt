package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.PacketMapImpl
import top.limbang.doctor.protocol.utils.getOrCreate
import java.util.*

class ModPacketRegistryImpl : IModPacketRegistry {
    private val modPacketMap: MutableMap<String, MutableMap<PacketDirection, MutableMap<ForgeProtocolState, IModPacketMap>>> =
        HashMap()

    override fun modPacketMap(channel: String, dir: PacketDirection, state: ForgeProtocolState): IModPacketMap {
        return modPacketMap.getOrCreate(channel) {
            EnumMap(PacketDirection::class.java)
        }.getOrCreate(dir) {
            EnumMap(ForgeProtocolState::class.java)
        }.getOrCreate(state) {
            DefaultModPacketMap()
        }
    }
}

class DefaultModPacketMap : IModPacketMap by PacketMapImpl()