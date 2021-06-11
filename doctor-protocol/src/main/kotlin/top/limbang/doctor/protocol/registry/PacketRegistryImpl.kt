package top.limbang.doctor.protocol.registry

import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.core.PacketDirection
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class PacketRegistryImpl : IPacketRegistry {

    private val packetMapC2S: MutableMap<ProtocolState, IVanillaPacketMap> = EnumMap(ProtocolState::class.java)
    private val packetMapS2C: MutableMap<ProtocolState, IVanillaPacketMap> = EnumMap(ProtocolState::class.java)

    override fun packetMap(dir: PacketDirection, state: ProtocolState): IVanillaPacketMap {
        return when (dir) {
            PacketDirection.C2S -> {
                if (!packetMapC2S.containsKey(state)) {
                    packetMapC2S[state] = VanillaPacketMap()
                }
                packetMapC2S[state]!!
            }
            PacketDirection.S2C -> {
                if (!packetMapS2C.containsKey(state)) {
                    packetMapS2C[state] = VanillaPacketMap()
                }
                packetMapS2C[state]!!
            }
        }
    }


}

class VanillaPacketMap : IVanillaPacketMap by PacketMapImpl()

