package top.fanua.doctor.plugin.forge.definations.fml2

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readByteArray
import top.fanua.doctor.protocol.extension.readString

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/1 下午9:49
 */
@Serializable
data class ConfigurationDataPacket(
    val fileName: String,
    val data: ByteArray,
    override var messageId: Int = 0
) : FML2Packet

class ConfigurationDataDecoder : PacketDecoder<ConfigurationDataPacket> {
    override fun decoder(buf: ByteBuf): ConfigurationDataPacket {
        val fileName = buf.readString()
        val data = buf.readByteArray()
        return ConfigurationDataPacket(fileName, data)
    }
}
