package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.*
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IForgePacketRegistry {
    /**
     * Channel包
     */
    fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState): IChannelPacketMap
    fun channelPacketMap(dir: PacketDirection, state: ForgeProtocolState, action: IChannelPacketMap.() -> Unit) =
        channelPacketMap(dir, state).run(action)

    fun channelPacketMap(state: ForgeProtocolState, action: DirectionActionChannel.() -> Unit) =
        DirectionActionChannel(this, state).run(action)

    /**
     * 模组包
     */
    fun modPacketMap(channel: String, dir: PacketDirection, state: ForgeProtocolState): IModPacketMap
    fun modPacketMap(
        channel: String,
        dir: PacketDirection,
        state: ForgeProtocolState,
        action: IModPacketMap.() -> Unit
    ) =
        modPacketMap(channel, dir, state).run(action)

    fun modPacketMap(channel: String, action: ChannelAction.() -> Unit) =
        ChannelAction(this, channel).run(action)

    fun registerGroup(group: IForgePacketGroup) {
        group.registerPackets(this)
    }
}


typealias IChannelPacketMap = IForgePacketMap<String, ChannelPacket>
typealias IModPacketMap = IForgePacketMap<Int, ModPacket>

interface IForgePacketMap<K, V : ForgePacketBase> {
    fun <T : V> decoder(packetKey: K): ForgePacketDecoder<T>
    fun <T : V> encoder(packetKey: K): ForgePacketEncoder<T>
    fun <T : V> packetKey(packetType: Class<T>): K
    fun <T : V> decoder(packetType: Class<T>): ForgePacketDecoder<T> {
        return decoder(packetKey(packetType))
    }

    fun <T : V> encoder(packetType: Class<T>): ForgePacketEncoder<T> {
        return encoder(packetKey(packetType))
    }

    fun <T : V> register(
        packetKey: K, packetType: Class<T>,
        encoder: ForgePacketEncoder<T>
    ): IForgePacketMap<K, V>

    fun <T : V> register(
        packetKey: K,
        packetType: Class<T>,
        decoder: ForgePacketDecoder<T>
    ): IForgePacketMap<K, V>

    fun <T : V> register(packetKey: K, decoder: ForgePacketDecoder<T>): IForgePacketMap<K, V> {
        return register(packetKey, decoder.packetClass(), decoder)
    }

    fun <T : V> register(packetKey: K, encoder: ForgePacketEncoder<T>): IForgePacketMap<K, V> {
        return register(packetKey, encoder.packetClass(), encoder)
    }

}



