package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.core.ProtocolException
import top.fanua.doctor.protocol.definition.play.client.CombatEvent.*
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.readVarInt


/**
 * ### Combat Event Packet
 * Originally used for metadata for twitch streaming circa 1.8. Now only used to display the game over screen (with enter combat and end combat completely ignored by the Notchain client)
 * - [event] Determines the layout of the remaining packet.
 * - [duration] Length of the combat in ticks.
 * - [entityID]
 *      1. ID of the primary opponent of the ended combat, or -1 if there is no obvious primary opponent.
 *      2. Entity ID of the player that died (should match the client's entity ID).
 * - [playerID] Entity ID of the player that died (should match the client's entity ID).
 * - [message] The death message.
 */
@Serializable
data class CombatEventPacket(
    val event: CombatEvent = CombatEvent.ENTER_COMBAT,
    val duration: Int? = null,
    val entityID: Int? = null,
    val playerID: Int? = null,
    val message: String? = null
) : Packet

/**
 * ### 战斗事件
 * - [ENTER_COMBAT] 进入战斗
 * - [END_COMBAT] 结束战斗
 * - [ENTITY_DEAD] 实体死亡
 */
enum class CombatEvent(val event: Int) {
    ENTER_COMBAT(0),
    END_COMBAT(1),
    ENTITY_DEAD(2);

    companion object {
        private val VALUES = values()
        fun getByEvent(value: Int) = VALUES.firstOrNull { it.event == value }
    }
}

class CombatEventDecoder : PacketDecoder<CombatEventPacket> {
    override fun decoder(buf: ByteBuf): CombatEventPacket {
        return when (val event = CombatEvent.getByEvent(buf.readVarInt())) {
            ENTER_COMBAT -> CombatEventPacket()
            END_COMBAT -> CombatEventPacket(
                event = event,
                duration = buf.readVarInt(),
                entityID = buf.readInt()
            )
            ENTITY_DEAD -> CombatEventPacket(
                event = event,
                playerID = buf.readVarInt(),
                entityID = buf.readInt(),
                message = buf.readString()
            )
            null -> throw ProtocolException("未识别的事件.")
        }
    }
}
