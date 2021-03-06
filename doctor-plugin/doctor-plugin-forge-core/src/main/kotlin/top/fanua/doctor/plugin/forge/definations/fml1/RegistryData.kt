package top.fanua.doctor.plugin.forge.definations.fml1

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.forge.api.FML1Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readString
import top.fanua.doctor.protocol.extension.readVarInt

/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 0:32
 */
@Serializable
data class RegistryDataPacket(
    val discriminator: Byte,
    val hasMore: Boolean,
    val name: String,
    val numberOfIds: Int,
    val ids: List<Ids>,
    val numberOfSubstitutions: Int,
    val substitutions: List<String>,
    val numberOfDummies: Int,
    val dummies: List<String>
) : FML1Packet

@Serializable
data class Ids(
    val name: String,
    val id: Int
)

class RegistryDataDecoder : PacketDecoder<RegistryDataPacket> {
    override fun decoder(buf: ByteBuf): RegistryDataPacket {
        val discriminator = buf.readByte()
        val hasMore = buf.readBoolean()
        val name = buf.readString()
        val numberOfIds = buf.readVarInt()
        val ids = mutableListOf<Ids>()
        for (i in 0 until numberOfIds) {
            ids.add(Ids(buf.readString(), buf.readVarInt()))
        }
        val numberOfSubstitutions = buf.readVarInt()
        val substitutions = mutableListOf<String>()
        for (i in 0 until numberOfSubstitutions) {
            substitutions.add(buf.readString())
        }
        val numberOfDummies = buf.readVarInt()
        val dummies = mutableListOf<String>()
        for (i in 0 until numberOfDummies) {
            dummies.add(buf.readString())
        }
        return RegistryDataPacket(
            discriminator = discriminator,
            hasMore = hasMore,
            name = name,
            numberOfIds = numberOfIds,
            ids = ids,
            numberOfSubstitutions = numberOfSubstitutions,
            substitutions = substitutions,
            numberOfDummies = numberOfDummies,
            dummies = dummies
        )
    }

}
