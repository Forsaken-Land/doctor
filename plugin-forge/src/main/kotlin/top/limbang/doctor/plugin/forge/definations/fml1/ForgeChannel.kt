package top.limbang.doctor.plugin.forge.definations.fml1

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML1Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 12:54
 */
@Serializable
data class ForgeChannelPacket(
    val string: String
) : FML1Packet

class ForgeChannelDecoder : PacketDecoder<ForgeChannelPacket> {
    override fun decoder(buf: ByteBuf): ForgeChannelPacket {
        val byteArray = ByteArray(buf.readableBytes())
        buf.readBytes(byteArray)
        return ForgeChannelPacket(string = String(byteArray))
    }

}