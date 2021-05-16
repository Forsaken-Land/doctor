package top.limbang.doctor.plugin.forge.handler

import io.netty.channel.Channel
import kotlinx.coroutines.runBlocking
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.plugin.forge.ATTR_FORGE_STATE
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.definations.fml.*
import top.limbang.doctor.plugin.forge.event.ForgeStateChange
import top.limbang.doctor.plugin.forge.event.ForgeStateChangeEventArgs
import top.limbang.doctor.protocol.api.Packet

/**
 * @author Doctor_Yin
 * @date 2021/4/30
 * @time 21:16
 */
class ForgeHandshakeListener(
    val a1Plugin: FML1Plugin
) : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Read) {
            val connection = it.context!!.channel().attr(Attributes.ATTR_CONNECTION).get()
            if (connection != null && it.message is Packet) {
                runBlocking {
                    handshake(connection, it.message as Packet, it.context!!.channel())
                }
            }
        }
    }

    private fun setForgeState(channel: Channel, state: ForgeProtocolState) {
        val from = channel.attr(ATTR_FORGE_STATE).getAndSet(state)
        a1Plugin.emit(ForgeStateChange, ForgeStateChangeEventArgs(from, state))
    }

    private var channels: List<String> = listOf()

    private fun handshake(connection: Connection, packet: Packet, channel: Channel) {
        when (packet) {
            is RegisterPacket -> {
                channels = packet.channels
                setForgeState(channel, ForgeProtocolState.HELLO)
            }
            is HelloServerPacket -> {
                //一个协议一个协议写
                connection.sendPacket(RegisterPacket(channels))
                connection.sendPacket(HelloClientPacket())
                connection.sendPacket(ModListPacket(a1Plugin.modList))

                setForgeState(channel, ForgeProtocolState.MODLIST)

            }

            is ModListPacket -> {
                setForgeState(channel, ForgeProtocolState.HANDSHAKE)
                connection.sendPacket(HandshakeAckPacket(phase = 2))
                setForgeState(channel, ForgeProtocolState.REGISTERDATA)
            }
            is RegistryDataPacket -> {
                if (!packet.hasMore) {
                    setForgeState(channel, ForgeProtocolState.HANDSHAKE)
                    connection.sendPacket(HandshakeAckPacket(phase = 3))
                }
            }
            is HandshakeAckPacket -> {
                if (packet.phase.toInt() == 2) connection.sendPacket(HandshakeAckPacket(phase = 5))
                if (packet.phase.toInt() == 3) {
                    connection.sendPacket(HandshakeAckPacket(phase = 5))
                    setForgeState(channel, ForgeProtocolState.PLAY)

                }
            }
//            is JoinGamePacket -> connection.sendPacket(ClientSettingPacket())
//
//            is CustomPayloadPacket -> {
//                connection.sendPacket(CustomPayloadPacket(string = packet.string))
//
//            }
//
//            is PlayerPositionAndLookPacket -> {
//                connection.sendPacket(TeleportConfirmPacket(packet.teleportId))
//                connection.sendPacket(ClientStatusPacket(ClientStatusEnum.PerformRespawn))
//            }
//            is CombatEventPacket -> {
//                if (packet.event == 2) {
//                    connection.sendPacket(ClientStatusPacket(ClientStatusEnum.PerformRespawn))
//                }
//            }

        }

    }

}