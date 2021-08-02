package top.fanua.doctor.client.listener

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.network.handler.oncePacket
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.protocol.definition.channel.RegisterPacket
import top.fanua.doctor.protocol.definition.play.client.*

/**
 * ### 游戏状态监听器
 */
class PlayListener : EventListener {
    private var entityId: Int = 0
    override fun initListen(emitter: EventEmitter) {

        //监听通道注册并回复
        emitter.replyPacket<RegisterPacket> {
            it
        }
        // 监听加入游戏包并回复
        emitter.replyPacket<JoinGamePacket> {
            if (it is JoinGamePacketType2) entityId = it.entityId
            ClientSettingPacket()
        }
        //1.7登录自动重生
        emitter.oncePacket<SKeepAlivePacket> {
            if (entityId != 0) {
                connection.sendPacket(ClientStatusPacket(ClientStatusEnum.PerformRespawn))
            }
        }
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
        // 监听受伤事件(1.7版本
        emitter.replyPacket<EntityStatusPacket> {
            // 未找到1.7的死亡包 用受到伤害替代
            if (it.entityID == entityId && it.entityStatus == 2.toByte()) {
                ClientStatusPacket(ClientStatusEnum.PerformRespawn)
            } else null
        }
        emitter.onPacket<PlayerPositionAndLookPacket> {
            connection.sendPacket(TeleportConfirmPacket(packet.teleportId))
            connection.sendPacket(ClientStatusPacket(ClientStatusEnum.PerformRespawn))
        }

        emitter.onPacket<CustomPayloadPacket> {
            packet.close()
        }

    }

}
