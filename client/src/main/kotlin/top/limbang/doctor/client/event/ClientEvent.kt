package top.limbang.doctor.client.event

import top.limbang.doctor.client.session.GameProfile
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.protocol.definition.play.client.*

/**
 * ### 登陆成功事件
 */
object LoginSuccessEvent : Event<GameProfile>

/**
 * ### 加入游戏事件
 */
object JoinGameEvent : Event<JoinGameArgs>
data class JoinGameArgs(
    val connection: Connection,
    val joinGamePacket: JoinGamePacket
)

/**
 * ### 聊天事件
 */
object ChatEvent : Event<ChatArgs>
data class ChatArgs(
    val connection: Connection,
    val chatPacket: ChatPacket
)

/**
 * ### 服务器难度事件
 */
object ServerDifficultyEvent : Event<ServerDifficultyPacket>

/**
 * ### tab补全事件
 */
object TabCompleteEvent : Event<TabCompleteArgs>
data class TabCompleteArgs(
    val connection: Connection,
    val sTabCompletePacket: STabCompletePacket
)

/**
 * chunkData事件
 */
object ChunkDataEvent : Event<ChunkDataArgs>
data class ChunkDataArgs(
    val connection: Connection,
    val chunkDataPacket: ChunkDataPacket
)
