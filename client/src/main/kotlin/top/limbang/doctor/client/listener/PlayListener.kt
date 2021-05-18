package top.limbang.doctor.client.listener

import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.event.EventListener
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.network.handler.replyPacket
import top.limbang.doctor.protocol.definition.play.client.*

/**
 * ### 游戏状态监听器
 */
class PlayListener : EventListener {
    override fun initListen(emitter: EventEmitter) {
        // 监听加入游戏包并回复
        emitter.replyPacket<JoinGamePacket>(ClientSettingPacket())
        // 监听心跳数据包并回复
        emitter.replyPacket<SKeepAlivePacket> { CKeepAlivePacket(it.keepAliveId) }
        // 监听战斗事件
        emitter.replyPacket<CombatEventPacket> {
            when (it.event) {
                // 事件等于实体死亡,就执行重生
                CombatEvent.ENTITY_DEAD -> ClientStatusPacket(ClientStatusEnum.PerformRespawn)
                else -> null
            }
        }

        emitter.onPacket<PlayerPositionAndLookPacket> {
            connection.sendPacket(TeleportConfirmPacket(packet.teleportId))
            connection.sendPacket(ClientStatusPacket(ClientStatusEnum.PerformRespawn))
        }

        emitter.onPacket<CustomPayloadPacket> {
            packet.rawData?.release()
        }

    }

}