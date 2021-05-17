package top.limbang.doctor.client.listener

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.network.handler.replyPacket
import top.limbang.doctor.protocol.definition.play.client.*
import top.limbang.doctor.protocol.entity.extra.CustomPayloadType

/**
 *
 * @author WarmthDawn
 * @since 2021-05-17
 */
class PlayListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        emitter.replyPacket<JoinGamePacket>(ClientSettingPacket())

        emitter.replyPacket<CustomPayloadPacket> {
            if (it.type() == CustomPayloadType.MC_Brand) {
                CustomPayloadPacket(CustomPayloadType.MC_Brand, it.data)
            } else {
                null
            }
        }
        emitter.replyPacket<CombatEventPacket> {
            if (it.event == 2) {
                ClientStatusPacket(ClientStatusEnum.PerformRespawn)
            } else {
                null
            }
        }

        emitter.onPacket<PlayerPositionAndLookPacket> {
            connection.sendPacket(TeleportConfirmPacket(packet.teleportId))
            connection.sendPacket(ClientStatusPacket(ClientStatusEnum.PerformRespawn))
        }

    }

}