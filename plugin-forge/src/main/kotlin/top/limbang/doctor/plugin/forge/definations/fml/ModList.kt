package top.limbang.doctor.plugin.forge.definations.fml

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.plugin.forge.api.ChannelPacket

/**
 * @author Doctor_Yin
 * @date 2021/4/30
 * @time 23:17
 */
@Serializable
data class ModListPacket(
    val discriminator: Byte,
    val numberOfMods: Int,
    val modList: Map<String, String>
) : ChannelPacket {
    constructor(modList: Map<String, String>) : this(2, modList.size, modList)
}

class ModListEncoder : PacketEncoder<ModListPacket> {
    override fun encode(buf: ByteBuf, packet: ModListPacket): ByteBuf {
        buf.writeByte(packet.discriminator.toInt())
        buf.writeVarInt(packet.numberOfMods)
        packet.modList.forEach {
            buf.writeString(it.key)
            buf.writeString(it.value)
        }
        return buf
    }

}

class ModListDecoder : PacketDecoder<ModListPacket> {
    override fun decoder(buf: ByteBuf): ModListPacket {
        val discriminator = buf.readByte()
        val numberOfMods = buf.readVarInt()
        val modList = mutableMapOf<String, String>()
        for (i in 0 until numberOfMods) {
            val modId = buf.readString()
            val version = buf.readString()
            modList[modId] = version
        }
        return ModListPacket(discriminator = discriminator, numberOfMods = numberOfMods, modList = modList)
    }

}