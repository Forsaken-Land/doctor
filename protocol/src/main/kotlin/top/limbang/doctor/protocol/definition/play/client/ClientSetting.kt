package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketEncoder

enum class MainHandEnum(val id: Int) {
    Left(0),
    Right(1);
}

@Serializable
data class ClientSettingPacket(
    val lang: String,
    val viewDistance: Byte,
    val chatMode: Int,
    val chatColors: Boolean,
    val displayedSkinParts: Int = displayedSkinParts(),
    val mainHand: Int
) : Packet {
    constructor() : this(
        "zh_cn",
        8,
        0,
        true,
        displayedSkinParts(),
        1
    )

    companion object {
        private fun displayedSkinParts(): Int {
            var i = 0
            for (j in 0 until 7) {
                i = 1 shl j
            }
            return i
        }

    }

    constructor(
        lang: String,
        viewDistance: Byte,
        chatMode: Int,
        chatColors: Boolean,
        displayedSkinParts: Int,
        mainHand: MainHandEnum
    ) : this(lang, viewDistance, chatMode, chatColors, displayedSkinParts, mainHand.id)

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