package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.querz.nbt.tag.CompoundTag
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.definition.play.client.Difficulty.*
import top.limbang.doctor.protocol.definition.play.client.GameMode.*
import top.limbang.doctor.protocol.definition.play.client.WorldType.*
import top.limbang.doctor.protocol.extension.readCompoundTag
import top.limbang.doctor.protocol.extension.readResourceLocation
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readVarInt
import top.limbang.doctor.protocol.utils.ResourceLocation


interface JoinGamePacket : Packet

/**
 * ### 加入游戏 版本340(前后
 * - [entityId] 玩家实体ID (EID)
 * - [isHardcore] 是否硬核模式
 * - [gameMode] 0: 生存模式, 1: 创造模式, 2: 探险模式, 3: 观察模式. 位3（0x8）是核心标志。
 * - [dimension] -1: 下界, 0: 主世界, 1: 末地; 另外，请注意这不是一个VarInt，而是一个普通Int。
 * - [difficulty] 0: 和平, 1: 简单, 2: 普通, 3: 困难
 * - [maxPlayers] 曾经被客户端用来绘制玩家列表，但现在被忽略了
 * - [worldType] 默认，平坦，大型生物群落，放大，定制，自选
 * - [reducedDebugInfo] 如果为真，Notchian客户端在调试屏幕上显示减少的信息。 对于开发中的服务器，几乎总为false。
 *
 */
@Serializable
data class JoinGamePacketType0(
    val entityId: Int,
    val isHardcore: Boolean,
    val gameMode: GameMode,
    val dimension: Int,
    val difficulty: Difficulty,
    val maxPlayers: Int,
    val worldType: WorldType,
    val reducedDebugInfo: Boolean
) : JoinGamePacket


/**
 * ### 加入游戏 版本340(后
 * - [entityId] 玩家实体ID (EID)
 * - [isHardcore] 是否硬核模式
 * - [gameMode] 0: 生存模式, 1: 创造模式, 2: 探险模式, 3: 观察模式.
 * - [previousGameMode] 0: 生存模式, 1: 创造模式, 2: 探险模式, 3: 观察模式. 不包括硬核标志。以前的游戏模式。如果没有以前的游戏模式，则默认为-1。 （需要更多信息）
 * - [worldCount] 以下数组的大小。
 * - [worldNames] 服务器上所有世界的标识符。
 * - [dimensionCodec] 这些的全部范围仍是未知的，但是该标记代表一个维度和生物群系注册表。参见下面的默认香草。
 * - [dimension] 有效尺寸是在此之前发送的每个尺寸注册表中定义的。该标签的结构是尺寸类型（请参见下文）。
 * - [worldName] 产生的世界名称。
 * - [hashedSeed] 世界种子的SHA-256哈希值的前8个字节。客户端使用过的生物群落噪声
 * - [maxPlayers] 曾经被客户端用来绘制玩家列表，但现在被忽略了
 * - [viewDistance] 渲染距离（2-32）。
 * - [reducedDebugInfo] 如果为真，Notchian客户端在调试屏幕上显示减少的信息。 对于开发中的服务器，几乎总为false。
 * - [enableRespawnScreen] 当doImmediateRespawn游戏规则为true时，设置为false。
 * - [isDebug] 如果世界是一个调试模式世界，则为true；否则为true。调试模式世界无法修改，并且具有预定义的块。
 * - [isFlat] 如果世界是一个超扁平的世界，那么为真；平面世界具有不同的虚空雾和y = 0而不是y = 63的地平线。
 *
 */
@Serializable
data class JoinGamePacketType1(
    val entityId: Int,
    val isHardcore: Boolean,
    val gameMode: GameMode,
    val previousGameMode: GameMode,
    val worldCount: Int,
    val worldNames: Set<ResourceLocation>,
    @Contextual
    val dimensionCodec: CompoundTag,
    @Contextual
    val dimension: CompoundTag,
    val worldName: String,
    val hashedSeed: Long,
    val maxPlayers: Int,
    val viewDistance: Int,
    val reducedDebugInfo: Boolean,
    val enableRespawnScreen: Boolean,
    val isDebug: Boolean,
    val isFlat: Boolean
) : JoinGamePacket

/**
 * ### 游戏模式
 * - [NOT_SET] 默认
 * - [SURVIVAL] 生存
 * - [CREATIVE] 创造
 * - [ADVENTURE] 冒险
 * - [SPECTATOR] 旁观
 */
enum class GameMode(var id: Int) {
    NOT_SET(-1),
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    companion object {
        private val VALUES = values()
        fun getByMode(value: Int) = VALUES.firstOrNull { it.id == value }
    }
}

/**
 * ### 游戏难度
 * - [PEACEFUL] 和平
 * - [EASY]     简单
 * - [NORMAL]   普通
 * - [HARD]     困难
 */
enum class Difficulty(private val id: Int) {
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    companion object {
        private val VALUES = values()
        fun getByDifficulty(value: Int) = VALUES.firstOrNull { it.id == value }
    }
}

/**
 * ### 世界类型
 * 默认，平坦，大型生物群落，放大，定制，自选
 *
 * - [DEFAULT] 默认
 * - [FLAT] 平坦
 * - [LARGE_BIOMES] 大型生物群落
 * - [AMPLIFIED] 放大
 * - [CUSTOMIZED] 定制
 * - [DEBUG_ALL_BLOCK_STATES] 调试所有块状态
 * - [DEFAULT_1_1]
 */
enum class WorldType(private val id: Int, private val type: String) {
    DEFAULT(0, "default"),
    FLAT(1, "flat"),
    LARGE_BIOMES(2, "largeBiomes"),
    AMPLIFIED(3, "amplified"),
    CUSTOMIZED(4, "customized"),
    DEBUG_ALL_BLOCK_STATES(5, "debug_all_block_states"),
    DEFAULT_1_1(8, "default_1_1");

    companion object {
        private val VALUES = values()
        fun getByName(value: String) = VALUES.firstOrNull { it.type == value }
    }

}

class JoinGameType0Decoder : PacketDecoder<JoinGamePacketType0> {
    override fun decoder(buf: ByteBuf): JoinGamePacketType0 {
        val entityId = buf.readInt()
        var i = buf.readUnsignedByte().toInt()
        val isHardcore = (i and 8) == 8
        i = i and -9
        val gameMode = GameMode.getByMode(i)!!
        val dimension = buf.readInt()
        val difficulty = Difficulty.getByDifficulty(buf.readUnsignedByte().toInt())!!
        val maxPlayers = buf.readUnsignedByte().toInt()
        var worldType = WorldType.getByName(buf.readString(16))
        if (worldType == null) worldType = DEFAULT
        val reducedDebugInfo = buf.readBoolean()
        return JoinGamePacketType0(
            entityId = entityId,
            isHardcore = isHardcore,
            gameMode = gameMode,
            dimension = dimension,
            difficulty = difficulty,
            maxPlayers = maxPlayers,
            worldType = worldType,
            reducedDebugInfo = reducedDebugInfo
        )

    }
}

class JoinGameType1Decoder : PacketDecoder<JoinGamePacketType1> {
    override fun decoder(buf: ByteBuf): JoinGamePacketType1 {
        val playerId = buf.readInt()
        val hardcoreMode = buf.readBoolean()
        val gameType = GameMode.getByMode(buf.readUnsignedByte().toInt())!!
        val previousGameMode = GameMode.getByMode(buf.readByte().toInt())!!
        val worldCount = buf.readVarInt()
        val worldNames = mutableSetOf<ResourceLocation>()
        for (j in 0 until worldCount) {
            worldNames.add(buf.readResourceLocation())
        }
        val dimensionCodec = buf.readCompoundTag()
        val dimension = buf.readCompoundTag()
        val worldName = buf.readResourceLocation().path
        val hashedSeed = buf.readLong()
        val maxPlayers = buf.readVarInt()
        val viewDistance = buf.readVarInt()
        val reducedDebugInfo = buf.readBoolean()
        val enableRespawnScreen = buf.readBoolean()
        val isDebug = buf.readBoolean()
        val isFlat = buf.readBoolean()
        return JoinGamePacketType1(
            playerId,
            hardcoreMode,
            gameType,
            previousGameMode,
            worldCount,
            worldNames,
            dimensionCodec,
            dimension,
            worldName,
            hashedSeed,
            maxPlayers,
            viewDistance,
            reducedDebugInfo,
            enableRespawnScreen,
            isDebug,
            isFlat
        )
    }
}

