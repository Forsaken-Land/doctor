package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 13:03
 */
@Serializable
data class JoinGamePacket(
    private var playerId: Int,
    private val hardcoreMode: Boolean,
    private val gameType: GameType,
    private val dimension: Int,
    private val difficulty: EnumDifficulty,
    private val maxPlayers: Int,
    private val worldType: WorldType,
    private val reducedDebugInfo: Boolean

) : Packet


class JoinGameDecoder : PacketDecoder<JoinGamePacket> {
    override fun decoder(buf: ByteBuf): JoinGamePacket {
        val playerId = buf.readInt()
        var i = buf.readUnsignedByte().toInt()
        val hardcoreMode = (i and 8) == 8
        i = i and -9
        val gameType = GameType.getByID(i)
        val dimension = buf.readInt()
        val difficulty = EnumDifficulty.byId(buf.readUnsignedByte().toInt())
        val maxPlayers = buf.readUnsignedByte().toInt()
        var worldType = WorldType.byName(buf.readString(16))
        if (worldType == null) worldType = WorldType.DEFAULT
        val reducedDebugInfo = buf.readBoolean()
        return JoinGamePacket(
            playerId = playerId,
            hardcoreMode = hardcoreMode,
            gameType = gameType,
            dimension = dimension,
            difficulty = difficulty,
            maxPlayers = maxPlayers,
            worldType = worldType,
            reducedDebugInfo = reducedDebugInfo
        )

    }

}

@Serializable
enum class GameType(
    private var id: Int,
    private val type: String,
    private var shortName: String
) {
    NOT_SET(-1, "", ""),
    SURVIVAL(0, "survival", "s"),
    CREATIVE(1, "creative", "c"),
    ADVENTURE(2, "adventure", "a"),
    SPECTATOR(3, "spectator", "sp");

    companion object {
        fun getByID(id: Int): GameType {
            for (gameType in values()) {
                if (gameType.id == id) {
                    return gameType
                }
            }
            return SURVIVAL
        }
    }
}

@Serializable
enum class EnumDifficulty(
    private val id: Int,
    private val translationKey: String
) {
    PEACEFUL(0, "options.difficulty.peaceful"),
    EASY(1, "options.difficulty.easy"),
    NORMAL(2, "options.difficulty.normal"),
    HARD(3, "options.difficulty.hard");

    companion object {
        fun byId(id: Int): EnumDifficulty {
            for (type in values()) {
                if (type.id == id) {
                    return type
                }
            }
            return NORMAL
        }
    }
}

@Serializable
//TODO 未完成
enum class WorldType(
    private val id: Int,
    private val type: String,
    private val version: Int,
    private val canBeCreated: Boolean,
    private val versioned: Boolean,
    private val hasInfoNotice: Boolean
) {
    DEFAULT(0, "default", 1),
    FLAT(1, "flat"),
    LARGE_BIOMES(2, "largeBiomes"),
    AMPLIFIED(3, "amplified"),
    CUSTOMIZED(4, "customized"),
    DEBUG_ALL_BLOCK_STATES(5, "debug_all_block_states"),
    DEFAULT_1_1(8, "default_1_1", 0);

    companion object {
        fun byName(type: String): WorldType? {
            for (worldType in values()) {
                if (worldType.name == type) {
                    return worldType
                }
            }
            return null
        }
    }

    constructor(id: Int, name: String) : this(id, name, 0)
    constructor(id: Int, name: String, version: Int) : this(
        type = name,
        version = version,
        canBeCreated = when (name) {
            "default_1_1" -> false
            else -> true
        },
        id = id,
        versioned = when (version) {
            1 -> true
            else -> false
        },
        hasInfoNotice = when (name) {
            "amplified" -> true
            else -> false
        }

    )
}