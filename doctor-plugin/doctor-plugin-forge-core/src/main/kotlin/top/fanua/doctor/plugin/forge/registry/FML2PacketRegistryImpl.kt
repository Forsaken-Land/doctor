package top.fanua.doctor.plugin.forge.registry

import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.PacketMapImpl
import top.fanua.doctor.protocol.utils.getOrCreate
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/2 下午5:54
 */
class FML2PacketRegistryImpl : IFML2PacketRegistry {

    private val fml2Map: MutableMap<PacketDirection, IFML2PacketMap> =
        EnumMap(PacketDirection::class.java)

    override fun fml2PacketMap(dir: PacketDirection): IFML2PacketMap {
        return fml2Map.getOrCreate(dir) { DefaultFML2PacketMap() }
    }
}

class DefaultFML2PacketMap : IFML2PacketMap by PacketMapImpl()
