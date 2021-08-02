package top.fanua.doctor.plugin.forge.definations.fml2

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.extension.*
import top.fanua.doctor.protocol.utils.ResourceLocation

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/1 下午7:52
 */
@Serializable
data class ModListPacket(
    val modCount: Int,
    val modNames: List<String>,
    val channelCount: Int,
    val channels: List<Channel>,
    val registryCount: Int,
    val registries: List<ResourceLocation>,
    override var messageId: Int = 0
) : FML2Packet {
    @Serializable
    data class Channel(
        val name: String,
        val marker: String
    )
}

class ModListDecoder : PacketDecoder<ModListPacket> {
    override fun decoder(buf: ByteBuf): ModListPacket {
        val modCount = buf.readVarInt()
        val modNames = mutableListOf<String>()
        for (i in 0 until modCount) {
            modNames.add(buf.readString())
        }
        val channelCount = buf.readVarInt()
        val channels = mutableListOf<ModListPacket.Channel>()
        for (j in 0 until channelCount) {
            channels.add(ModListPacket.Channel(buf.readString(), buf.readString()))
        }
        val registryCount = buf.readVarInt()
        val registries = mutableListOf<ResourceLocation>()
        for (k in 0 until registryCount) {
            registries.add(buf.readResourceLocation())
        }
        return ModListPacket(modCount, modNames, channelCount, channels, registryCount, registries)
    }

}

class ModListEncoder : PacketEncoder<ModListPacket> {
    override fun encode(buf: ByteBuf, packet: ModListPacket): ByteBuf {
        buf.writeVarInt(packet.modCount)
        packet.modNames.forEach {
            buf.writeString(it)
        }
        buf.writeVarInt(packet.channelCount)
        packet.channels.forEach {
            buf.writeString(it.name)
            buf.writeString(it.marker)
        }
        buf.writeVarInt(0)
//        packet.registries.forEach {
//            buf.writeResourceLocation(it)
//            buf.writeString("")
//        }
        return buf
    }
}
