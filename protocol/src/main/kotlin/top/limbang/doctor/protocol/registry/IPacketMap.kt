package top.limbang.doctor.protocol.registry

import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.utils.packetClass

interface IPacketMap<K, V : Packet> {
    fun <T : V> decoder(packetKey: K): PacketDecoder<T>
    fun <T : V> encoder(packetKey: K): PacketEncoder<T>
    fun <T : V> packetKey(packetType: Class<T>): K
    fun <T : V> decoder(packetType: Class<T>): PacketDecoder<T> {
        return decoder(packetKey(packetType))
    }

    fun <T : V> encoder(packetType: Class<T>): PacketEncoder<T> {
        return encoder(packetKey(packetType))
    }


    fun <T : V> register(packetKey: K, packetType: Class<T>, encoder: PacketEncoder<T>): IPacketMap<K, V>
    fun <T : V> register(packetKey: K, packetType: Class<T>, decoder: PacketDecoder<T>): IPacketMap<K, V>

    fun <T : V> register(packetKey: K, decoder: PacketDecoder<T>): IPacketMap<K, V> {
        return register(packetKey, decoder.packetClass(), decoder)
    }

    fun <T : V> register(packetKey: K, encoder: PacketEncoder<T>): IPacketMap<K, V> {
        return register(packetKey, encoder.packetClass(), encoder)
    }
}
