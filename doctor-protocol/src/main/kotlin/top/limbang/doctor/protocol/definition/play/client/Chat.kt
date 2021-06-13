package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.core.annotation.VersionExpandPacket
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readUUID
import java.util.*


interface ChatPacket : Packet {
    val json: String
    val type: ChatType
}

@Serializable
@VersionExpandPacket(ChatPacket::class)
data class ChatType0Packet(
    override val json: String,
    override val type: ChatType
) : ChatPacket

@Serializable
@VersionExpandPacket(ChatPacket::class)
data class ChatType1Packet(
    override val json: String,
    override val type: ChatType,
    @Contextual
    val sender: UUID
) : ChatPacket

class ChatType0Decoder : PacketDecoder<ChatPacket> {
    override fun decoder(buf: ByteBuf): ChatPacket {
        val json = buf.readString()
        val type = ChatType.byId(buf.readByte())
        return ChatType0Packet(json, type)
    }
}

class ChatType1Decoder : PacketDecoder<ChatPacket> {
    override fun decoder(buf: ByteBuf): ChatPacket {
        val json = buf.readString()
        val type = ChatType.byId(buf.readByte())
        val uuid = buf.readUUID()
        return ChatType1Packet(json, type, uuid)
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
