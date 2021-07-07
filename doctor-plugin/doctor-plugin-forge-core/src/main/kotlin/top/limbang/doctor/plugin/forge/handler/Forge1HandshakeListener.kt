package top.limbang.doctor.plugin.forge.handler

import io.netty.channel.Channel
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.plugin.forge.ATTR_FORGE_STATE
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.definations.fml1.*
import top.limbang.doctor.plugin.forge.event.ForgeStateChange
import top.limbang.doctor.plugin.forge.event.ForgeStateChangeEventArgs
import top.limbang.doctor.protocol.definition.play.client.DisconnectPacket

/**
 * @author Doctor_Yin
 * @date 2021/4/30
 * @time 21:16
 */
class Forge1HandshakeListener(
    val fmlPlugin: FML1Plugin
) : EventListener {

    override fun initListen(emitter: EventEmitter) {
        emitter.onPacket<DisconnectPacket> {
            setForgeState(ctx.channel(), ForgeProtocolState.HELLO) //TODO 应该确定
        }

        emitter.onPacket<HelloServerPacket> {
            setForgeState(ctx.channel(), ForgeProtocolState.HELLO)
            connection.sendPacket(HelloClientPacket())

            setForgeState(ctx.channel(), ForgeProtocolState.MODLIST)
            connection.sendPacket(ModListPacket(fmlPlugin.modList))

        }

        emitter.onPacket<ModListPacket> {
            setForgeState(ctx.channel(), ForgeProtocolState.HANDSHAKE)
            connection.sendPacket(HandshakeAckPacket(phase = 2))
            if (fmlPlugin.modList["Forge"]!!.startsWith("10.12.") or fmlPlugin.modList["Forge"]!!.startsWith("10.13.")) {
                setForgeState(ctx.channel(), ForgeProtocolState.MODIDDATA)
            } else {
                setForgeState(ctx.channel(), ForgeProtocolState.REGISTERDATA)
            }
        }
        emitter.onPacket<RegistryDataPacket> {
            if (!packet.hasMore) {
                setForgeState(ctx.channel(), ForgeProtocolState.HANDSHAKE)
                connection.sendPacket(HandshakeAckPacket(phase = 3))
            }
        }
        emitter.onPacket<ModIdDataPacket> {
            setForgeState(ctx.channel(), ForgeProtocolState.HANDSHAKE)
            connection.sendPacket(HandshakeAckPacket(phase = 3))
        }
        emitter.onPacket<HandshakeAckPacket> {
            if (packet.phase.toInt() == 2) connection.sendPacket(HandshakeAckPacket(phase = 5))
            if (packet.phase.toInt() == 3) {
                connection.sendPacket(HandshakeAckPacket(phase = 5))
                setForgeState(ctx.channel(), ForgeProtocolState.PLAY)

            }
        }


    }

    private fun registerChannels(channels: List<String>) {
        fmlPlugin.channelPacketRegistry.channels = channels
    }

    private fun setForgeState(channel: Channel, state: ForgeProtocolState) {
        val from = channel.attr(ATTR_FORGE_STATE).getAndSet(state)
        fmlPlugin.emit(ForgeStateChange, ForgeStateChangeEventArgs(from, state))
    }


}
