package top.fanua.doctor.plugin.forge.registry

import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.registry.GroupRegistrable
import top.fanua.doctor.protocol.registry.IPacketMap

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

data class ChannelAndId(
    val channel: String,
    val id: Int
)
typealias IFML2PacketMap = IPacketMap<ChannelAndId, FML2Packet>

class DirectionActionFML2(private val registry: IFML2PacketRegistry) {
    fun whenC2S(action: IFML2PacketMap.() -> Unit) {
        action(registry.fml2PacketMap(PacketDirection.C2S))
    }

    fun whenS2C(action: IFML2PacketMap.() -> Unit) {
        action(registry.fml2PacketMap(PacketDirection.S2C))
    }
}

