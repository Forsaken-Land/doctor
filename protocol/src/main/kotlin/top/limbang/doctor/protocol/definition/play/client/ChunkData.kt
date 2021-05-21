package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.querz.nbt.tag.CompoundTag
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.extension.readCompoundTag
import top.limbang.doctor.protocol.extension.readVarInt


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
@Suppress("ArrayInDataClass")
@Serializable
data class ChunkDataPacket(
    val chunkX: Int,
    val chunkZ: Int,
    val fullChunk: Boolean,
    val availableSections: Int,
    var buffer: ByteArray,
    var tileEntityTags: MutableList<@Contextual CompoundTag>
) : Packet

class ChunkDataDecoder : PacketDecoder<ChunkDataPacket> {
    override fun decoder(buf: ByteBuf): ChunkDataPacket {
        val chunkX = buf.readInt()
        val chunkZ = buf.readInt()
        val fullChunk = buf.readBoolean()
        val availableSections = buf.readVarInt()
        val bufferSize = buf.readVarInt()
        val buffer = ByteArray(bufferSize)
        buf.readBytes(buffer)
        val tagSize = buf.readVarInt()
        val tileEntityTags: MutableList<CompoundTag> = arrayListOf()

        for (k in 0 until tagSize) {
            tileEntityTags.add(buf.readCompoundTag())
        }

        return ChunkDataPacket(
            chunkX = chunkX,
            chunkZ = chunkZ,
            fullChunk = fullChunk,
            availableSections = availableSections,
            buffer = buffer,
            tileEntityTags = tileEntityTags
        )
    }
}