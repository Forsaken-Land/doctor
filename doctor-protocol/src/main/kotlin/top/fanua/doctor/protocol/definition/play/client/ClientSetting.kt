package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.writeString
import top.fanua.doctor.protocol.extension.writeVarInt

/**
 * ### 客户端配置
 *
 * - [lang] 语言
 * - [viewDistance] 可视距离
 * - [chatMode]
 * - [chatColors]
 * - [displayedSkinParts]
 * - [mainHand]
 */
@Serializable
data class ClientSettingPacket(
    val lang: String = "zh_cn",
    val viewDistance: Byte = 8,
    val chatMode: ChatMode = ChatMode.ENABLE,
    val chatColors: Boolean = true,
    val displayedSkinParts: Int = displayedSkinParts(),//TODO 需要重写
    val mainHand: MainHandEnum = MainHandEnum.Right,
    val disableTextFiltering: Boolean? = null
) : Packet {

    companion object {
        private fun displayedSkinParts(): Int {
            var i = 0
            for (j in 0 until 7) {
                i = 1 shl j
            }
            return i
        }

    }

}

enum class MainHandEnum(val id: Int) {
    Left(0),
    Right(1);

    companion object {
        private val VALUES = values()
        fun getValues(value: Int) = VALUES.firstOrNull { it.id == value }
    }
}

enum class ChatMode(val id: Int) {
    ENABLE(0),
    COMMANDS(1),
    HIDDEN(2);

    companion object {
        private val VALUES = values()
        fun getValues(value: Int) = VALUES.firstOrNull { it.id == value }
    }
}

class ClientSettingEncoder : PacketEncoder<ClientSettingPacket> {
    override fun encode(buf: ByteBuf, packet: ClientSettingPacket): ByteBuf {
        buf.writeString(packet.lang)
        buf.writeByte(packet.viewDistance.toInt())
        buf.writeVarInt(packet.chatMode.id)
        buf.writeBoolean(packet.chatColors)
        buf.writeByte(packet.displayedSkinParts)
        buf.writeVarInt(packet.mainHand.id)
        if (packet.disableTextFiltering != null) {
            buf.writeBoolean(packet.disableTextFiltering)
        }
        return buf
    }
}
