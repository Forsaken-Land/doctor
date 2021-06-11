package top.limbang.doctor.client.handler

import top.limbang.doctor.client.event.*
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.utils.connection
import top.limbang.doctor.protocol.definition.play.client.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/28:22:24
 */
class PacketForwardingHandler : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.on(ConnectionEvent.Read) {
            when (it.message) {
                is JoinGamePacketType0, is JoinGamePacketType1 -> emitter.emit(
                    JoinGameEvent, JoinGameArgs(it.context!!.connection(), it.message as JoinGamePacket)
                )
                is ServerDifficultyType0Packet, is ServerDifficultyType1Packet -> emitter.emit(
                    ServerDifficultyEvent,
                    it.message as ServerDifficultyPacket
                )
                is ChatType0Packet, is ChatType1Packet -> emitter.emit(
                    ChatEvent,
                    ChatArgs(it.context!!.connection(), it.message as ChatPacket)
                )
                is STabCompleteType0Packet, is STabCompleteType1Packet -> emitter.emit(
                    TabCompleteEvent,
                    TabCompleteArgs(it.context!!.connection(), it.message as STabCompletePacket)
                )
            }
        }
    }
}


