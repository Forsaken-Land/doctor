package top.fanua.doctor.plugin.forge.definations.fml1

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.forge.api.FML1Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.readVarInt

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
) : FML1Packet

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
) : FML1Packet {
    constructor() : this("FML|HS", 1, 2)
}

class HelloClientEncoder : PacketEncoder<HelloClientPacket> {
    override fun encode(buf: ByteBuf, packet: HelloClientPacket): ByteBuf {
        buf.writeByte(packet.discriminator.toInt())
        buf.writeByte(packet.FMLProtocolVersion.toInt())
        return buf
    }

}

