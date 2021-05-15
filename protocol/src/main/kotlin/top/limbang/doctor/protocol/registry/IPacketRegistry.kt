package top.limbang.doctor.protocol.registry

import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IPacketRegistry : GroupRegistrable<IPacketRegistry>{
    fun packetMap(dir: PacketDirection, state: ProtocolState): IVanillaPacketMap
    fun packetMap(dir: PacketDirection, state: ProtocolState, action: IVanillaPacketMap.() -> Unit) =
        packetMap(dir, state).run(action)

    fun packetMap(state: ProtocolState, action: DirectionAction.() -> Unit) = DirectionAction(this, state).run(action)

}

class DirectionAction(private val registry: IPacketRegistry, private val state: ProtocolState) {
    fun whenC2S(action: IVanillaPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.C2S, state))
    }

    fun whenS2C(action: IVanillaPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.S2C, state))
    }
}


typealias IVanillaPacketMap = IPacketMap<Int, Packet>
