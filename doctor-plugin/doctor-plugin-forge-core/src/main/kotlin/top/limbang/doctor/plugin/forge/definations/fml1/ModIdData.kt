package top.limbang.doctor.plugin.forge.definations.fml1

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.plugin.forge.api.FML1Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.extension.readVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/8:2:36
 */
@Serializable
data class ModIdDataPacket(
    val discriminator: Byte,
    val mappingLength: Int,
    val mapping: List<Mapping>,
    val blockSubstitutionsLength: Int,
    val blockSubstitutions: List<String>,
    val itemSubstitutionsLength: Int,
    val itemSubstitutions: List<String>
) : FML1Packet {
    @Serializable
    data class Mapping(
        val name: String,
        val id: Int
    )
}

class ModIdDataDecoder : PacketDecoder<ModIdDataPacket> {
    override fun decoder(buf: ByteBuf): ModIdDataPacket {
        val discriminator = buf.readByte()

        val mappingLength = buf.readVarInt()
        val mapping = 0.until(mappingLength).map {
            ModIdDataPacket.Mapping(buf.readString(), buf.readVarInt())
        }.toList()

        val blockSubstitutionsLength = buf.readVarInt()
        val blockSubstitutions = 0.until(blockSubstitutionsLength).map { buf.readString() }.toList()

        val itemSubstitutionsLength = buf.readVarInt()
        val itemSubstitutions = 0.until(itemSubstitutionsLength).map { buf.readString() }.toList()

        return ModIdDataPacket(
            discriminator,
            mappingLength,
            mapping,
            blockSubstitutionsLength,
            blockSubstitutions,
            itemSubstitutionsLength,
            itemSubstitutions
        )
    }
}
