package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.definition.play.client.Difficulty.*
import top.limbang.doctor.protocol.definition.play.client.GameMode.*
import top.limbang.doctor.protocol.definition.play.client.WorldType.*
import top.limbang.doctor.protocol.extension.readString

/**
 * ### 加入游戏
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
data class JoinGamePacket(
    private val entityId: Int,
    private val isHardcore: Boolean,
    private val gameMode: GameMode,
    private val dimension: Int,
    private val difficulty: Difficulty,
    private val maxPlayers: Int,
    private val worldType: WorldType,
    private val reducedDebugInfo: Boolean

) : Packet

/**
 * ### 游戏模式
 * - [SURVIVAL] 生存
 * - [CREATIVE] 创造
 * - [ADVENTURE] 冒险
 * - [SPECTATOR] 旁观
 */
enum class GameMode(var id: Int) {
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

class JoinGameDecoder : PacketDecoder<JoinGamePacket> {
    override fun decoder(buf: ByteBuf): JoinGamePacket {
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
        return JoinGamePacket(
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