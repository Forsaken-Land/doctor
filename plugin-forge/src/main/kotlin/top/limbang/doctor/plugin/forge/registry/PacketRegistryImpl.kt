package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ForgePacketBase
import top.limbang.doctor.plugin.forge.api.ForgePacketDecoder
import top.limbang.doctor.plugin.forge.api.ForgePacketEncoder
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.core.PacketDirection
import top.limbang.doctor.protocol.core.ProtocolException
import top.limbang.doctor.core.cast
import java.util.*

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class ForgePacketRegistryImpl : IForgePacketRegistry {

    private val channelMap: MutableMap<PacketDirection, MutableMap<ForgeProtocolState, IChannelPacketMap>> =
        EnumMap(PacketDirection::class.java)

    private val modPacketMap: MutableMap<String, MutableMap<PacketDirection, MutableMap<ForgeProtocolState, IModPacketMap>>> =
        HashMap()

    private fun <K, V> MutableMap<K, V>.getOrCreate(key: K, default: () -> V): V {
        if (!this.containsKey(key)) {
            this[key] = default()
        }
        return this[key]!!
    }

    override fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState): IChannelPacketMap {

        return channelMap.getOrCreate(dir) {
            EnumMap(ForgeProtocolState::class.java)
        }.getOrCreate(state) { DefaultChannelPacketMap() }
    }
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


class DefaultChannelPacketMap : IChannelPacketMap by DefaultForgePacketMap()
class DefaultModPacketMap : IModPacketMap by DefaultForgePacketMap()

class DefaultForgePacketMap<K, V : ForgePacketBase> : IForgePacketMap<K, V> {
    private val decoderMap: MutableMap<K, ForgePacketDecoder<*>> = HashMap()
    private val encoderMap: MutableMap<K, ForgePacketEncoder<*>> = HashMap()
    private val packetTypeMap: MutableMap<Class<out V>, K> = HashMap()

    override fun <T : V> decoder(packetKey: K): ForgePacketDecoder<T> {
        return decoderMap[packetKey]?.cast() ?: throw ProtocolException("未找到协议包$packetKey 对应的解码实现.")
    }

    override fun <T : V> encoder(packetKey: K): ForgePacketEncoder<T> {
        return encoderMap[packetKey]?.cast() ?: throw ProtocolException("未找到协议包$packetKey 对应的编码实现.")
    }

    override fun <T : V> packetKey(packetType: Class<T>): K {
        return packetTypeMap[packetType] ?: throw ProtocolException("协议包${packetType.simpleName} 未注册.")
    }

    private fun <T : V> addTypeMap(packetId: K, packetType: Class<T>) {
        if (packetTypeMap.containsKey(packetType)) {
            if (packetTypeMap[packetType] != packetId) {
                throw ProtocolException("未能成功注册Packet：$packetType 已经被注册为 ${packetTypeMap[packetType]}， 而不是$packetId")
            }
            return
        }
        packetTypeMap[packetType] = packetId
    }

    override fun <T : V> register(
        packetKey: K,
        packetType: Class<T>,
        encoder: ForgePacketEncoder<T>
    ): IForgePacketMap<K, V> {
        addTypeMap(packetKey, packetType)
        encoderMap[packetKey] = encoder
        return this
    }

    override fun <T : V> register(
        packetKey: K,
        packetType: Class<T>,
        decoder: ForgePacketDecoder<T>
    ): IForgePacketMap<K, V> {
        addTypeMap(packetKey, packetType)
        decoderMap[packetKey] = decoder
        return this
    }
}