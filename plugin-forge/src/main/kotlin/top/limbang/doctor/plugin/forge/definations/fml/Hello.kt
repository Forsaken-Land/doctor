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
 * @time 21:05
 */

/**
 * ServerHello包
 * 参考 https://wiki.vg/Minecraft_Forge_Handshake
 */
@Serializable
data class HelloServerPacket(
    val discriminator: Byte,
    val FMLProtocolVersion: Byte,
    val overrideDimension: Int
) : ChannelPacket

class HelloServerDecoder : PacketDecoder<HelloServerPacket> {
    override fun decoder(buf: ByteBuf): HelloServerPacket {
        val discriminator = buf.readByte()
        val fMLProtocolVersion = buf.readByte()
        val overrideDimension = buf.readVarInt()
        return HelloServerPacket(
            discriminator = discriminator,
            FMLProtocolVersion = fMLProtocolVersion,
            overrideDimension = overrideDimension
        )
    }

}


/**
 * ClientHello包
 * 参考 https://wiki.vg/Minecraft_Forge_Handshake
 */
@Serializable
data class HelloClientPacket(
    val channel: String = "FML|HS",
    val discriminator: Byte,
    val FMLProtocolVersion: Byte
) : ChannelPacket {
    constructor() : this("FML|HS", 1, 2)
}

class HelloClientEncoder : PacketEncoder<HelloClientPacket> {
    override fun encode(buf: ByteBuf, packet: HelloClientPacket): ByteBuf {
        buf.writeByte(packet.discriminator.toInt())
        buf.writeByte(packet.FMLProtocolVersion.toInt())
        return buf
    }

}

