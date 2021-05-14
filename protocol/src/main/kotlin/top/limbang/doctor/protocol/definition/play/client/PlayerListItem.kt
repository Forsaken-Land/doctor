package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

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
//        val action = buf.readEnumValue(Action::class.java)

        TODO()
//        val i = buf.readVarInt()
//        for (j in 0 until i) {
//
//            var gameprofile: GameProfile? = null
//            var k = 0
//            var gametype: GameType? = null
//            var itextcomponent: ITextComponent? = null
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
enum class Action {
    ADD_PLAYER,
    UPDATE_GAME_MODE,
    UPDATE_LATENCY,
    UPDATE_DISPLAY_NAME,
    REMOVE_PLAYER;
}
