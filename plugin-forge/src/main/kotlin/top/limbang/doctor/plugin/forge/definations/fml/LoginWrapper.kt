package top.limbang.doctor.plugin.forge.definations.fml

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.ChannelPacket
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.extension.readByteArray
import top.limbang.doctor.protocol.extension.readResourceLocation
import top.limbang.doctor.protocol.utils.ResourceLocation

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/30:16:09
 */
data class LoginWrapperPacket(
    val resourceLocation: ResourceLocation,
    val data: String
) : ChannelPacket


class LoginWrapperDecoder : PacketDecoder<LoginWrapperPacket> {
    override fun decoder(buf: ByteBuf): LoginWrapperPacket {
        val resourceLocation = buf.readResourceLocation()
//        println(buf.readString())
//        val byteArray = ByteArray(buf.readableBytes())
//        buf.readBytes(byteArray)
//        byteArray.toList().forEach { byte ->
//            println(byte)
//            println(String(ByteArray(1).also {
//                it[0] = byte
//            }))
//        }
//        println(String(byteArray))
        val string = String(buf.readByteArray())
        return LoginWrapperPacket(resourceLocation, string)
    }
}
