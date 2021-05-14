package top.limbang.doctor.protocol.registry

import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.core.ProtocolException
import top.limbang.doctor.protocol.utils.cast
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class PacketRegistryImpl : IPacketRegistry {

    private val packetMapC2S: MutableMap<ProtocolState, IPacketMap> = EnumMap(ProtocolState::class.java)
    private val packetMapS2C: MutableMap<ProtocolState, IPacketMap> = EnumMap(ProtocolState::class.java)

    override fun packetMap(dir: PacketDirection, state: ProtocolState): IPacketMap {
        return when (dir) {
            PacketDirection.C2S -> {
                if (!packetMapC2S.containsKey(state)) {
                    packetMapC2S[state] = DefaultPacketMap()
                }
                packetMapC2S[state]!!
            }
            PacketDirection.S2C -> {
                if (!packetMapS2C.containsKey(state)) {
                    packetMapS2C[state] = DefaultPacketMap()
                }
                packetMapS2C[state]!!
            }
        }
    }


}

class DefaultPacketMap : IPacketMap {
    private val decoderMap: MutableMap<Int, PacketDecoder<*>> = HashMap()
    private val encoderMap: MutableMap<Int, PacketEncoder<*>> = HashMap()
    private val packetTypeMap: MutableMap<Class<out Packet>, Int> = HashMap()

    override fun <T : Packet> decoder(packetId: Int): PacketDecoder<T> {
        return decoderMap[packetId]?.cast() ?: throw ProtocolException("未找到协议包$packetId 对应的解码实现.")
    }

    override fun <T : Packet> encoder(packetId: Int): PacketEncoder<T> {
        return encoderMap[packetId]?.cast() ?: throw ProtocolException("未找到协议包$packetId 对应的编码实现.")
    }

    override fun <T : Packet> packetId(packetType: Class<T>): Int {
        return packetTypeMap[packetType] ?: throw ProtocolException("协议包${packetType.simpleName} 未注册.")
    }

    private fun <T : Packet> addTypeMap(packetId: Int, packetType: Class<T>) {
        if (packetTypeMap.containsKey(packetType)) {
            if (packetTypeMap[packetType] != packetId) {
                throw ProtocolException("未能成功注册Packet：$packetType 已经被注册为 ${packetTypeMap[packetType]}， 而不是$packetId")
            }
            return
        }
        packetTypeMap[packetType] = packetId
    }

    override fun <T : Packet> register(packetId: Int, packetType: Class<T>, encoder: PacketEncoder<T>): IPacketMap {
        addTypeMap(packetId, packetType)
        encoderMap[packetId] = encoder
        return this
    }

    override fun <T : Packet> register(packetId: Int, packetType: Class<T>, decoder: PacketDecoder<T>): IPacketMap {
        addTypeMap(packetId, packetType)
        decoderMap[packetId] = decoder
        return this
    }

}