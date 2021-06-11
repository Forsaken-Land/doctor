package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.registry.GroupRegistrable
import top.limbang.doctor.protocol.registry.IPacketMap

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/2 下午5:45
 */
interface IFML2PacketRegistry : GroupRegistrable<IFML2PacketRegistry> {
    /**
     * FML2包
     */

    fun fml2PacketMap(dir: PacketDirection): IFML2PacketMap
    fun fml2PacketMap(dir: PacketDirection, action: IFML2PacketMap.() -> Unit) =
        fml2PacketMap(dir).run(action)

    fun fml2PacketMap(action: DirectionActionFML2.() -> Unit) =
        DirectionActionFML2(this).run(action)

}
typealias IFML2PacketMap = IPacketMap<Int, FML2Packet>

class DirectionActionFML2(private val registry: IFML2PacketRegistry) {
    fun whenC2S(action: IFML2PacketMap.() -> Unit) {
        action(registry.fml2PacketMap(PacketDirection.C2S))
    }

    fun whenS2C(action: IFML2PacketMap.() -> Unit) {
        action(registry.fml2PacketMap(PacketDirection.S2C))
    }
}

