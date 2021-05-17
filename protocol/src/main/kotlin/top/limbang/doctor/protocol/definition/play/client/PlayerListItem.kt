package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.entity.text.ChatGsonSerializer
import top.limbang.doctor.protocol.extension.readEnumValue
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readUUID
import top.limbang.doctor.protocol.extension.readVarInt

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 21:19
 */
@Serializable
data class PlayerListItemPacket(
    val action: Action,
    @Contextual
    val buf: ByteBuf
) : Packet

class PlayerListItemDecoder : PacketDecoder<PlayerListItemPacket> {
    override fun decoder(buf: ByteBuf): PlayerListItemPacket {
        val action = buf.readEnumValue(Action::class.java)

        val i = buf.readVarInt()
        for (j in 0 until i) {
            when (action) {
                Action.ADD_PLAYER -> {
                    println(buf.readUUID())
                    println(buf.readString(16))
                    val size = buf.readVarInt()
                    for (k in 0 until size) {
                        println(buf.readString() + ":" + buf.readString())
                        if (buf.readBoolean()) {
                            println(buf.readString())
                        }
                    }
                    println("game:${buf.readVarInt()}")
                    println("ping:${buf.readVarInt()}")
                    if (buf.readBoolean()) {
                        val chat = ChatGsonSerializer.jsonToChat(buf.readString())
                        println("nick:${chat.getFormattedText()}")
                    }
                }
                Action.UPDATE_LATENCY -> {
                    println(buf.readUUID())
                    println(buf.readVarInt())
                }
                Action.REMOVE_PLAYER -> {
                    println(buf.readUUID())
                }
                Action.UPDATE_GAME_MODE -> {
                    println(buf.readUUID())
                    println(buf.readVarInt())
                }
                Action.UPDATE_DISPLAY_NAME -> {
                    println(buf.readUUID())
                    if (buf.readBoolean()) {
                        val chat = ChatGsonSerializer.jsonToChat(buf.readString())
                        println(chat.getFormattedText())
                    }
                }
            }
        }
        return PlayerListItemPacket(action, buf)
//
//            when (action) {
//                Action.ADD_PLAYER -> {
//                    gameprofile = GameProfile(buf.readUniqueId(), buf.readString(16))
//                    val l = buf.readVarInt()
//                    var i1 = 0
//                    while (i1 < l) {
//                        val s = buf.readString(32767)
//                        val s1 = buf.readString(32767)
//                        if (buf.readBoolean()) {
//                            gameprofile.getProperties().put(s, Property(s, s1, buf.readString(32767)))
//                        } else {
//                            gameprofile.getProperties().put(s, Property(s, s1))
//                        }
//                        ++i1
//                    }
//                    gametype = GameType.getByID(buf.readVarInt())
//                    k = buf.readVarInt()
//                    if (buf.readBoolean()) {
//                        itextcomponent = buf.readTextComponent()
//                    }
//                }
//                Action.UPDATE_GAME_MODE -> {
//                    gameprofile = GameProfile(buf.readUniqueId(), null as String)
//                    gametype = GameType.getByID(buf.readVarInt())
//                }
//                Action.UPDATE_LATENCY -> {
//                    gameprofile = GameProfile(buf.readUniqueId(), null as String?)
//                    k = buf.readVarInt()
//                }
//                Action.UPDATE_DISPLAY_NAME -> {
//                    gameprofile = GameProfile(buf.readUniqueId(), null as String?)
//                    if (buf.readBoolean()) {
//                        itextcomponent = buf.readTextComponent()
//                    }
//                }
//                Action.REMOVE_PLAYER -> gameprofile =
//                    GameProfile(buf.readUniqueId(), null as String?)
//            }
//
//            this.players.add(
//                net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData(
//                    gameprofile,
//                    k,
//                    gametype,
//                    itextcomponent
//                )
//            )
//        }
//        return PlayerListItemPacket(action = action, buf = buf)
    }

}

@Serializable
enum class Action(val id: Int) {
    ADD_PLAYER(0),
    UPDATE_GAME_MODE(1),
    UPDATE_LATENCY(2),
    UPDATE_DISPLAY_NAME(3),
    REMOVE_PLAYER(4);
}
