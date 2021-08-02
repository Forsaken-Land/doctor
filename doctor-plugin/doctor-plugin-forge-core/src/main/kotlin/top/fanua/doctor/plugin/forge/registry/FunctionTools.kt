package top.fanua.doctor.plugin.forge.registry

import top.fanua.doctor.protocol.core.PacketDirection

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class ChannelAction(private val registry: IModPacketRegistry, private val channel: String) {
    fun with(action: DirectionActionMod.() -> Unit) =
        DirectionActionMod(registry, channel).run(action)
}

class DirectionActionMod(
    private val registry: IModPacketRegistry,
    private val channel: String
) {
    fun whenC2S(action: IModPacketMap.() -> Unit) {
        action(registry.modPacketMap(channel, PacketDirection.C2S))
    }

    fun whenS2C(action: IModPacketMap.() -> Unit) {
        action(registry.modPacketMap(channel, PacketDirection.S2C))
    }
}

