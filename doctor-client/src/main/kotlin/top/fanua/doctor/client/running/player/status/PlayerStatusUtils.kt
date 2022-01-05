package top.fanua.doctor.client.running.player.status

import kotlinx.serialization.Serializable
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.protocol.definition.play.client.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/9 10:33
 */
class PlayerStatusUtils(
    client: MinecraftClient
) {
    private val playerStatus = PlayerStatus()
    private var entityId = 0

    init {
        client.onPacket<UpdateHealthPacket> {
            playerStatus.food = packet.food.toDouble()
            playerStatus.heal = packet.health.toDouble()
        }.onPacket<JoinGamePacket> {
            when (packet) {
                is JoinGamePacketType0 -> entityId = (packet as JoinGamePacketType0).entityId
                is JoinGamePacketType1 -> entityId = (packet as JoinGamePacketType1).entityId
                is JoinGamePacketType2 -> entityId = (packet as JoinGamePacketType2).entityId
            }
        }.onPacket<EntityPropertiesPacket> {
            if (packet.entityId == entityId) {
                playerStatus.maxHealth = packet.property.find { it.key == PropertyKey.MaxHealth }?.value ?: 20.0
                playerStatus.armor = packet.property.find { it.key == PropertyKey.Armor }?.value ?: 0.0
            }
        }
    }

    fun getStatus(): PlayerStatus = playerStatus

}

@Serializable
data class PlayerStatus(
    var maxHealth: Double = 0.0,
    var heal: Double = 0.0,
    var food: Double = 0.0,
    var armor: Double = 0.0
)