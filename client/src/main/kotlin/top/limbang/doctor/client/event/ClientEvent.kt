package top.limbang.doctor.client.event

import top.limbang.doctor.client.session.GameProfile
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.definition.play.client.JoinGamePacket
import top.limbang.doctor.protocol.definition.play.client.STabCompletePacket
import top.limbang.doctor.protocol.definition.play.client.ServerDifficultyPacket

/**
 * ### 登陆成功事件
 */
object LoginSuccessEvent : Event<GameProfile>
object JoinGameEvent : Event<JoinGameArgs>
object ChatEvent : Event<ChatArgs>
data class JoinGameArgs(
    val connection: Connection,
    val joinGamePacket: JoinGamePacket
)

data class ChatArgs(
    val connection: Connection,
    val chatPacket: ChatPacket
)

object ServerDifficultyEvent : Event<ServerDifficultyPacket>
object TabCompleteEvent : Event<TabCompleteArgs>
data class TabCompleteArgs(
    val connection: Connection,
    val sTabCompletePacket: STabCompletePacket
)
