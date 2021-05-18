package top.limbang.doctor.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.running.TpsUtils
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.handler.onPacket
import top.limbang.doctor.protocol.definition.play.client.Action
import top.limbang.doctor.protocol.definition.play.client.ChatPacket
import top.limbang.doctor.protocol.definition.play.client.PlayerInfo
import top.limbang.doctor.protocol.definition.play.client.PlayerListItemPacket
import top.limbang.doctor.protocol.entity.text.ChatGsonSerializer
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


private val logger: Logger = LoggerFactory.getLogger("MAIN")
fun main() {

    val pros = Properties()
    val file = FileInputStream("local.properties")
    pros.load(file)

    val host = pros["host"] as String
    val port = (pros["port"] as String).toInt()
    val username = pros["username"] as String
    val password = pros["password"] as String
    val authServerUrl = pros["authServerUrl"] as String
    val sessionServerUrl = pros["sessionServerUrl"] as String
    val players = mutableMapOf<UUID, PlayerInfo>()
    var playerUpdateTime = LocalDateTime.now()

    val client = MinecraftClient()
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .start(host, port)

    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(2000)
        client.reconnect()
    }.onPacket<ChatPacket> {
        val chat = ChatGsonSerializer.jsonToChat(packet.json)
        logger.info(chat.getFormattedText())

    }.onPacket<PlayerListItemPacket> {
        playerUpdateTime = LocalDateTime.now()
        when (packet.action) {
            Action.ADD_PLAYER -> {
                for (player in packet.players) {
                    players[player.UUID] = player
                }
            }
            Action.UPDATE_GAME_MODE -> {
                for (player in packet.players) {
                    val thisPlayer = players[player.UUID] ?: throw RuntimeException("1错误？${player.name}") //理论上不会发生此问题
                    thisPlayer.gameMode = player.gameMode
                    players[player.UUID] = thisPlayer
                }
            }
            Action.UPDATE_LATENCY -> {
                for (player in packet.players) {
                    val thisPlayer = players[player.UUID] ?: throw RuntimeException("2错误？") //理论上不会发生此问题
                    thisPlayer.ping = player.ping
                    players[player.UUID] = thisPlayer
                }
            }
            Action.UPDATE_DISPLAY_NAME -> {
                for (player in packet.players) {
                    val thisPlayer = players[player.UUID] ?: throw RuntimeException("3错误？") //理论上不会发生此问题
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

    val tps = TpsUtils(client)

    while (true) {
        when (val msg = readLine()) {
            "tps" -> {
                try {
                    val result = tps.getTps()
                    logger.info(result.toString())
                } catch (e: Exception) {
                }
            }


            "list" -> {
                logger.info("玩家数量:${players.size}")
                logger.info("更新时间:${playerUpdateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 hh:mm:ss"))}")
                players.map {
                    logger.info("name:${it.value.name} ping:${it.value.ping} gameMode:${it.value.gameMode}")
                }

            }
            else -> client.sendMessage(msg!!)
        }

    }
}


