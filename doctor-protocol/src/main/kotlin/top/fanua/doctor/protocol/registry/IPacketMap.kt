package top.fanua.doctor.protocol.registry

import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.utils.packetClass

/**
 * 表示同一个方向，同一种类型的协议包注册表
 * @param K 包注册的key，如原版包为int, channel包为string
 * @param V 包类型
 */
interface IPacketMap<K, V : Packet> {
    /**
     * 获取解码器
     */
    fun <T : V> decoder(packetKey: K): PacketDecoder<T>

    /**
     * 获取编码器
     */
    fun <T : V> encoder(packetKey: K): PacketEncoder<T>

    /**
     * 根据包类型查询包key
     */
    fun <T : V> packetKey(packetType: Class<T>): K

    /**
     * 根据类型获取解码器
     */
    fun <T : V> decoder(packetType: Class<T>): PacketDecoder<T> {
        return decoder(packetKey(packetType))
    }

    /**
     * 根据类型获取编码器
     */
    fun <T : V> encoder(packetType: Class<T>): PacketEncoder<T> {
        return encoder(packetKey(packetType))
    }


    /**
     * 注册编码器
     */
    fun <T : V> register(packetKey: K, packetType: Class<T>, encoder: PacketEncoder<T>): IPacketMap<K, V>

    /**
     * 注册解码器
     */
    fun <T : V> register(packetKey: K, packetType: Class<T>, decoder: PacketDecoder<T>): IPacketMap<K, V>

    /**
     * 注册编码器（自动获取包类型）
     */
    fun <T : V> register(packetKey: K, decoder: PacketDecoder<T>): IPacketMap<K, V> {
        return register(packetKey, decoder.packetClass(), decoder)
    }

    /**
     * 注册解码器（自动获取包类型）
     */
    fun <T : V> register(packetKey: K, encoder: PacketEncoder<T>): IPacketMap<K, V> {
        return register(packetKey, encoder.packetClass(), encoder)
    }
}
