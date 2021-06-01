package top.limbang.doctor.plugin.forge.definations.fml1

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML1Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.extension.writeString

/**
 * @author Doctor_Yin
 * @date 2021/4/30
 * @time 17:42
 */

/**
 * ### 解码通道
 *
 * - [channels] 通道信息
 */
@Serializable
data class RegisterPacket(
    val channels: List<String>
) : FML1Packet

class RegisterEncoder : PacketEncoder<RegisterPacket> {
    override fun encode(buf: ByteBuf, packet: RegisterPacket): ByteBuf {
        packet.channels.map {
            buf.writeString(it)
            buf.writeByte(0x00)
        }
        return buf
    }

}

class RegisterDecoder : PacketDecoder<RegisterPacket> {
    override fun decoder(buf: ByteBuf): RegisterPacket {
        val byteArray = ByteArray(buf.readableBytes())
        buf.readBytes(byteArray)
        val temp: MutableList<Byte> = mutableListOf()
        val channels = mutableListOf<String>()
        byteArray.map {
            if (it.toInt() != 0x00) temp.add(it)
            else {
                channels.add(String(temp.toByteArray()))
                temp.clear()
            }
        }
        return RegisterPacket(channels = channels)
    }

}