package top.limbang.doctor.client.running

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.Action
import top.limbang.doctor.protocol.definition.play.client.PlayerInfo
import top.limbang.doctor.protocol.definition.play.client.PlayerListItemPacket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/22:18:51
 */
class PlayerUtils(
    client: MinecraftClient
) {
    private val players = mutableMapOf<UUID, PlayerInfo>()
    private var playerUpdateTime: LocalDateTime = LocalDateTime.now()

    init {
        client.onPacket<PlayerListItemPacket> {
            playerUpdateTime = LocalDateTime.now()
            when (packet.action) {
                Action.ADD_PLAYER -> {
                    for (player in packet.players) {
                        players[player.UUID] = player
                    }
                }
                Action.UPDATE_GAME_MODE -> {
                    for (player in packet.players) {
                        val thisPlayer =
                            players[player.UUID]!!
                        thisPlayer.gameMode = player.gameMode
                        players[player.UUID] = thisPlayer
                    }
                }
                Action.UPDATE_LATENCY -> {
                    for (player in packet.players) {
                        val thisPlayer = players[player.UUID]!!
                        thisPlayer.ping = player.ping
                        players[player.UUID] = thisPlayer
                    }
                }
                Action.UPDATE_DISPLAY_NAME -> {
                    for (player in packet.players) {
                        val thisPlayer = players[player.UUID]!!
                        thisPlayer.hasDisplayName = player.hasDisplayName
                        thisPlayer.displayName = player.displayName
                        players[player.UUID] = thisPlayer
                    }
                }
                Action.REMOVE_PLAYER -> {
                    for (player in packet.players) {
                        players.remove(player.UUID)
                    }
                }
            }
        }

    }

    fun getPlayers(): PlayerTab {
        return PlayerTab(playerUpdateTime, players)
    }
}

@Serializable
data class PlayerTab(
    val updateTime: String,
    val players: Map<@Contextual UUID, PlayerInfo>
) {
    constructor(updateTime: LocalDateTime, players: Map<@Contextual UUID, PlayerInfo>) : this(
        updateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")), players
    )
}
