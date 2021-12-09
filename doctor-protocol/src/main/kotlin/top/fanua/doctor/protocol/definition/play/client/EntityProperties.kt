package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.readUUID
import top.fanua.doctor.protocol.extension.readVarInt
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/9 9:40
 */
@Serializable
data class EntityPropertiesPacket(
    val entityId: Int,
    val size: Int,
    val property: List<Property>
) : Packet

@Serializable
data class Property(
    val key: PropertyKey,
    val other: String,
    val value: Double,
    val size: Int,
    val modifiers: List<Modifier>
)

@Serializable
data class Modifier(
    @Contextual
    val uuid: UUID,
    val amount: Double,
    val operation: Byte
)

class EntityPropertiesDecoder : PacketDecoder<EntityPropertiesPacket> {
    override fun decoder(buf: ByteBuf): EntityPropertiesPacket {
        val entityId = buf.readVarInt()
        val size = buf.readInt()
        val property = mutableListOf<Property>()
        for (i in 0 until size) {
            val str = buf.readString()
            val key = PropertyKey.values().find { it.key.replace("_", "").equals(str.replace("_", ""), true) }
            val value = buf.readDouble()
            val modifierSize = buf.readVarInt()
            val modifiers = mutableListOf<Modifier>()
            for (j in 0 until modifierSize) {
                val uuid = buf.readUUID()
                val amount = buf.readDouble()
                val operation = buf.readByte()
                modifiers.add(Modifier(uuid, amount, operation))
            }
            if (key != null) property.add(Property(key, "", value, modifierSize, modifiers))
            else property.add(Property(PropertyKey.Other, str, value, size, modifiers))
        }
        return EntityPropertiesPacket(entityId, size, property)
    }
}

enum class PropertyKey(val key: String) {
    MaxHealth("generic.max_health"),
    Range("generic.follow_range"),
    Resistance("generic.knockback_resistance"),
    MoveSpeed("generic.movement_speed"),
    AttackDamage("generic.attack_damage"),
    AttackSpeed("generic.attack_speed"),
    FlySpeed("generic.flying_speed"),
    Armor("generic.armor"),
    ArmorToughness("generic.armor_toughness"),
    AttackKnockback("generic.attack_knockback"),
    Luck("generic.luck"),
    JumpStrength("horse.jump_strength"),
    SpawnReinforcementsChance("zombie.spawn_reinforcements"),
    PlayerReachDistance("generic.reachDistance"),
    SwimmingSpeed("forge.swimSpeed"),
    Other("null")
}

