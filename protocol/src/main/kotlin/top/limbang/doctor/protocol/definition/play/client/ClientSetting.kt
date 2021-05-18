package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder

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
    val chatMode: Int = 0,
    val chatColors: Boolean = true,
    val displayedSkinParts: Int = displayedSkinParts(),
    val mainHand: Int = 1
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
}

class ClientSettingEncoder : PacketEncoder<ClientSettingPacket> {
    override fun encode(buf: ByteBuf, packet: ClientSettingPacket): ByteBuf {
        buf.writeString(packet.lang)
        buf.writeByte(packet.viewDistance.toInt())
        buf.writeVarInt(packet.chatMode)
        buf.writeBoolean(packet.chatColors)
        buf.writeByte(packet.displayedSkinParts)
        buf.writeVarInt(packet.mainHand)
        return buf
    }
}