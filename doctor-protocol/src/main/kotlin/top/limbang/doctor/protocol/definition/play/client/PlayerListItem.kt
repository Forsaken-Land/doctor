package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.definition.play.client.Action.*
import top.limbang.doctor.protocol.entity.text.ChatSerializer
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readUUID
import top.limbang.doctor.protocol.extension.readVarInt
import java.util.*

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 21:19
 */
@Serializable
data class PlayerListItemPacket(
    val action: Action,
    val numberOfPlayerInfo: Int,
    val players: List<PlayerInfo>
) : Packet {
    override fun toString(): String {
        return "PlayerListItemPacket(action=$action, numberOfPlayerInfo=$numberOfPlayerInfo, $players)"
    }
}

class PlayerListItemDecoder : PacketDecoder<PlayerListItemPacket> {
    override fun decoder(buf: ByteBuf): PlayerListItemPacket {
        val action = Action.getById(buf.readVarInt())!!

        val i = buf.readVarInt()
        val players = mutableListOf<PlayerInfo>()
        for (j in 0 until i) {
            val uuid = buf.readUUID()
            val playerInfo = when (action) {
                ADD_PLAYER -> {
                    val name = buf.readString(32)
                    val size = buf.readVarInt()
                    val propertyList = mutableListOf<PlayerInfo.Property>()
                    for (k in 0 until size) {
                        val propertyName = buf.readString()
                        val propertyValue = buf.readString()
                        val property = if (buf.readBoolean()) {
                            PlayerInfo.Property(propertyName, propertyValue, true, buf.readString())
                        } else {
                            PlayerInfo.Property(propertyName, propertyValue)
                        }
                        propertyList.add(property)
                    }
                    val gameMode = GameMode.getByMode(buf.readVarInt())!!
                    val ping = buf.readVarInt()
                    val hasDisplayName = buf.readBoolean()
                    if (hasDisplayName) {
                        val chat = ChatSerializer.jsonToChat(buf.readString())
                        val nickname = chat.getFormattedText()
                        PlayerInfo(uuid, name, size, propertyList, ping, gameMode, hasDisplayName, nickname)
                    } else PlayerInfo(uuid, name, size, propertyList, ping, gameMode, hasDisplayName)


                }
                UPDATE_LATENCY -> {
                    val ping = buf.readVarInt()
                    PlayerInfo(uuid, ping = ping)
                }
                REMOVE_PLAYER -> {
                    PlayerInfo(uuid)
                }
                UPDATE_GAME_MODE -> {
                    val gameMode = GameMode.getByMode(buf.readVarInt())!!
                    PlayerInfo(uuid, gameMode = gameMode)
                }
                UPDATE_DISPLAY_NAME -> {
                    if (buf.readBoolean()) {
                        val chat = ChatSerializer.jsonToChat(buf.readString())
                        PlayerInfo(uuid, hasDisplayName = true, displayName = chat.getFormattedText())
                    } else PlayerInfo(uuid, hasDisplayName = false)
                }
            }
            players.add(playerInfo)
        }
        return PlayerListItemPacket(action, i, players)
    }


}

/**
 * [ADD_PLAYER] 向TAB菜单添加玩家
 *
 * [UPDATE_GAME_MODE] 更新TAB菜单内玩家的gameMode
 *
 * [UPDATE_LATENCY] 更新TAB菜单内玩家的ping
 *
 * [UPDATE_DISPLAY_NAME] 更新TAB菜单内玩家的nick
 *
 * [REMOVE_PLAYER] 删除TAB菜单内的玩家
 */
@Serializable
enum class Action(val id: Int) {
    ADD_PLAYER(0),
    UPDATE_GAME_MODE(1),
    UPDATE_LATENCY(2),
    UPDATE_DISPLAY_NAME(3),
    REMOVE_PLAYER(4);

    companion object {
        private val VALUES = values()
        fun getById(value: Int) = VALUES.firstOrNull { it.id == value }
    }
}

@Serializable
data class PlayerInfo(
    @Contextual
    val UUID: UUID,
    val name: String? = null,
    val numberOfProperty: Int? = null,
    val properties: List<Property>? = null,
    var ping: Int? = null,
    var gameMode: GameMode? = null,
    var hasDisplayName: Boolean? = null,
    var displayName: String? = null
) {

    @Serializable
    data class Property(
        val name: String,
        val value: String,
        val isSigned: Boolean = false,
        val signature: String? = null
    )

    override fun toString(): String {
        return if (hasDisplayName == true) {
            "PlayerInfo(UUID=$UUID, name=$name, numberOfProperty=$numberOfProperty, ping=$ping, gameMode=$gameMode, hasDisplayName=$hasDisplayName, displayName=$displayName)"
        } else {
            "PlayerInfo(UUID=$UUID, name=$name, numberOfProperty=$numberOfProperty, ping=$ping, gameMode=$gameMode)"
        }

    }
}


