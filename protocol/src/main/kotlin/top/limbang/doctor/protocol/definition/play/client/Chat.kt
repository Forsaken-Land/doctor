package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder


@Serializable
data class ChatPacket(
    val json: String,
    val type: ChatType
) : Packet

class ChatDecoder : PacketDecoder<ChatPacket> {
    override fun decoder(buf: ByteBuf): ChatPacket {
        val json = buf.readString()
        val type = ChatType.byId(buf.readByte())
        return ChatPacket(json, type)
    }
}

enum class ChatType(val id: Byte) {
    CHAT(0),
    SYSTEM(1),
    GAME_INFO(2);

    companion object {
        fun byId(id: Byte): ChatType {
            for (chatType in values()) {
                if (id == chatType.id) {
                    return chatType
                }
            }
            return CHAT
        }
    }
}