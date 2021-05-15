package top.limbang.doctor.plugin.forge.definations.fml

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.ChannelPacket
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 12:54
 */
@Serializable
data class ForgeChannelPacket(
    val string: String
) : ChannelPacket

class ForgeChannelDecoder : PacketDecoder<ForgeChannelPacket> {
    override fun decoder(buf: ByteBuf): ForgeChannelPacket {
        val byteArray = ByteArray(buf.readableBytes())
        buf.readBytes(byteArray)
        return ForgeChannelPacket(string = String(byteArray))
    }

}