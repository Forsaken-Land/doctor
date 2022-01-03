package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.querz.nbt.tag.CompoundTag
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.entity.BlockStorage
import top.fanua.doctor.protocol.entity.Chunk
import top.fanua.doctor.protocol.entity.NibbleArray3d
import top.fanua.doctor.protocol.entity.Section
import top.fanua.doctor.protocol.extension.readCompoundTag
import top.fanua.doctor.protocol.extension.readVarInt


/**
 * ### Chunk Data
 * The server only sends skylight information for chunk pillars in the Overworld, it's up to the client to know in which dimension the player is currently located. You can also infer this information from the primary bitmask and the amount of uncompressed bytes sent. This packet also sends all block entities in the chunk (though sending them is not required; it is still legal to send them with Block Entity Data later).
 * - [chunkX] Chunk coordinate (block coordinate divided by 16, rounded down).
 * - [chunkZ] Chunk coordinate (block coordinate divided by 16, rounded down).
 * - [fullChunk] See Chunk Format.
 *
 *
 *
 *
 */
interface ChunkDataPacket : Packet

@Serializable
data class ChunkDataType0Packet(
    val chunkX: Int,
    val chunkZ: Int,
    val fullChunk: Boolean,
    val availableSections: Int,
    var chunk: Chunk,
    var tileEntityTags: MutableList<@Contextual CompoundTag>
) : ChunkDataPacket

@Suppress("ArrayInDataClass")
@Serializable
data class ChunkDataType1Packet(
    val chunkX: Int,
    val chunkZ: Int,
    val fullChunk: Boolean,
    val primaryBitMask: Int,
    @Contextual
    val heightmaps: CompoundTag,
    val biomesLength: Int?,
    val biomes: List<Int>?,
    val size: Int,
    val data: ByteArray,
    val numberOfBlockEntities: Int,
    val blockEntities: List<@Contextual CompoundTag>
) : ChunkDataPacket

class ChunkDataType0Decoder : PacketDecoder<ChunkDataType0Packet> {
    override fun decoder(buf: ByteBuf): ChunkDataType0Packet {
        val chunkX = buf.readInt()
        val chunkZ = buf.readInt()
        val fullChunk = buf.readBoolean()
        val availableSections = buf.readVarInt()
        val bufferSize = buf.readVarInt()
        val buffer = buf.readBytes(bufferSize)
        val list = mutableMapOf<Int, Section?>()
        for (i in 0 until 16) {
            if (availableSections and (1 shl i) == 0) {
                list[i] = null
            } else {
                val blocks = BlockStorage(buffer.readUnsignedByte().toInt(), buffer)
                val blockLight = NibbleArray3d(buffer)
                val skyLight = NibbleArray3d(buffer)
                list[i] = Section(i, blocks, blockLight, skyLight)
            }
        }
        val blockBiomesArray = if (fullChunk) {
            val array = ByteArray(256)
            buffer.readBytes(array)
            array
        } else null
        buffer.release()
        val tagSize = buf.readVarInt()
        val tileEntityTags: MutableList<CompoundTag> = arrayListOf()

        for (k in 0 until tagSize) {
            tileEntityTags.add(buf.readCompoundTag()!!)
        }

        return ChunkDataType0Packet(
            chunkX = chunkX,
            chunkZ = chunkZ,
            fullChunk = fullChunk,
            availableSections = availableSections,
            chunk = Chunk(chunkX, chunkZ, list, blockBiomesArray),
            tileEntityTags = tileEntityTags
        )
    }
}

class ChunkDataType1Decoder : PacketDecoder<ChunkDataType1Packet> {
    override fun decoder(buf: ByteBuf): ChunkDataType1Packet {
        val chunkX = buf.readInt()
        val chunkZ = buf.readInt()
        val fullChunk = buf.readBoolean()
        val primaryBitMask = buf.readVarInt()
        val heightmaps = buf.readCompoundTag()!!
        val biomesLength = if (fullChunk) {
            buf.readVarInt()
        } else {
            null
        }
        val biomes = if (biomesLength != null) {
            val list = mutableListOf<Int>()
            for (biomes in 0 until biomesLength) {
                list.add(buf.readVarInt())
            }
            list
        } else {
            null
        }
        val size = buf.readVarInt()
        val data = ByteArray(size)
        buf.readBytes(data)
        val numberOfBlockEntities = buf.readVarInt()
        val blockEntities = mutableListOf<CompoundTag>()
        for (blockEntity in 0 until numberOfBlockEntities) {
            blockEntities.add(buf.readCompoundTag()!!)
        }
        return ChunkDataType1Packet(
            chunkX,
            chunkZ,
            fullChunk,
            primaryBitMask,
            heightmaps,
            biomesLength,
            biomes,
            size,
            data,
            numberOfBlockEntities,
            blockEntities
        )
    }

}
