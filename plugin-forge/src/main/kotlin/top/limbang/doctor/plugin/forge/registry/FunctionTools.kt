package top.limbang.doctor.plugin.forge.registry

import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class ChannelAction(private val registry: IForgePacketRegistry, private val channel: String) {
    fun with(state: ForgeProtocolState, action: DirectionActionMod.() -> Unit) =
        DirectionActionMod(registry, state, channel).run(action)
}

class DirectionActionMod(
    private val registry: IForgePacketRegistry,
    private val state: ForgeProtocolState,
    private val channel: String
) {
    fun whenC2S(action: IModPacketMap.() -> Unit) {
        action(registry.modPacketMap(channel, PacketDirection.C2S, state))
    }

    fun whenS2C(action: IModPacketMap.() -> Unit) {
        action(registry.modPacketMap(channel, PacketDirection.S2C, state))
    }
}

class DirectionActionChannel(private val registry: IForgePacketRegistry, private val state: ForgeProtocolState) {
    fun whenC2S(action: IChannelPacketMap.() -> Unit) {
        action(registry.channelPacketMap(PacketDirection.C2S, state))
    }

    fun whenS2C(action: IChannelPacketMap.() -> Unit) {
        action(registry.channelPacketMap(PacketDirection.S2C, state))
    }
}