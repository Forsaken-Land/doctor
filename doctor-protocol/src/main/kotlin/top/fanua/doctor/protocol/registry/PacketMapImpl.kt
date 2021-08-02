package top.fanua.doctor.protocol.registry

import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.core.ProtocolException
import top.fanua.doctor.protocol.utils.cast

class PacketMapImpl<K, V : Packet> : IPacketMap<K, V> {
    private val decoderMap: MutableMap<K, PacketDecoder<*>> = HashMap()
    private val encoderMap: MutableMap<K, PacketEncoder<*>> = HashMap()
    private val packetTypeMap: MutableMap<Class<out V>, K> = HashMap()

    override fun <T : V> decoder(packetKey: K): PacketDecoder<T> {
        return decoderMap[packetKey]?.cast() ?: throw ProtocolException("未找到协议包$packetKey 对应的解码实现.")
    }

    override fun <T : V> encoder(packetKey: K): PacketEncoder<T> {
        return encoderMap[packetKey]?.cast() ?: throw ProtocolException("未找到协议包$packetKey 对应的编码实现.")
    }

    override fun <T : V> packetKey(packetType: Class<T>): K {
        return packetTypeMap[packetType] ?: throw ProtocolException("协议包${packetType.simpleName} 未注册.")
    }

    private fun <T : V> addTypeMap(packetKey: K, packetType: Class<T>) {
        if (packetTypeMap.containsKey(packetType)) {
            if (packetTypeMap[packetType] != packetKey) {
                throw ProtocolException("未能成功注册Packet：$packetType 已经被注册为 ${packetTypeMap[packetType]}， 而不是$packetKey")
            }
            return
        }
        packetTypeMap[packetType] = packetKey
    }

    override fun <T : V> register(packetKey: K, packetType: Class<T>, encoder: PacketEncoder<T>): IPacketMap<K, V> {
        addTypeMap(packetKey, packetType)
        encoderMap[packetKey] = encoder
        return this
    }

    override fun <T : V> register(packetKey: K, packetType: Class<T>, decoder: PacketDecoder<T>): IPacketMap<K, V> {
        addTypeMap(packetKey, packetType)
        decoderMap[packetKey] = decoder
        return this
    }

}
