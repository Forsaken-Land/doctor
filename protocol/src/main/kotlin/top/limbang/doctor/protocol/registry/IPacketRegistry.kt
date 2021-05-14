package top.limbang.doctor.protocol.registry

import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.utils.packetClass

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IPacketRegistry {
    fun packetMap(dir: PacketDirection, state: ProtocolState): IPacketMap
    fun packetMap(dir: PacketDirection, state: ProtocolState, action: IPacketMap.() -> Unit) =
        packetMap(dir, state).run(action)

    fun packetMap(state: ProtocolState, action: DirectionAction.() -> Unit) = DirectionAction(this, state).run(action)

    fun registerGroup(group: ICommonPacketGroup) {
        group.registerPackets(this)
    }
}

class DirectionAction(private val registry: IPacketRegistry, private val state: ProtocolState) {
    fun whenC2S(action: IPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.C2S, state))
    }

    fun whenS2C(action: IPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.S2C, state))
    }
}


interface IPacketMap {
    fun <T : Packet> decoder(packetId: Int): PacketDecoder<T>
    fun <T : Packet> encoder(packetId: Int): PacketEncoder<T>
    fun <T : Packet> packetId(packetType: Class<T>): Int
    fun <T : Packet> decoder(packetType: Class<T>): PacketDecoder<T> {
        return decoder(packetId(packetType))
    }

    fun <T : Packet> encoder(packetType: Class<T>): PacketEncoder<T> {
        return encoder(packetId(packetType))
    }


    fun <T : Packet> register(packetId: Int, packetType: Class<T>, encoder: PacketEncoder<T>): IPacketMap
    fun <T : Packet> register(packetId: Int, packetType: Class<T>, decoder: PacketDecoder<T>): IPacketMap

    fun <T : Packet> register(packetId: Int, decoder: PacketDecoder<T>): IPacketMap {
        return register(packetId, decoder.packetClass(), decoder)
    }

    fun <T : Packet> register(packetId: Int, encoder: PacketEncoder<T>): IPacketMap {
        return register(packetId, encoder.packetClass(), encoder)
    }
}

enum class PacketDirection {
    C2S,
    S2C
}
