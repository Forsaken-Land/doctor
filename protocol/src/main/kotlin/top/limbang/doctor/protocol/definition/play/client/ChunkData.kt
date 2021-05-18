package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.entity.nbt.NbtBase
import top.limbang.doctor.protocol.extension.readNbt
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
@Serializable
data class ChunkDataPacket(
    val chunkX: Int,
    val chunkZ: Int,
    val fullChunk: Boolean,
    val availableSections: Int,
    val heightmaps: NbtBase,
    val size: Int
) : Packet

class ChunkDataDecoder : PacketDecoder<ChunkDataPacket> {
    override fun decoder(buf: ByteBuf): ChunkDataPacket {
        val chunkX = buf.readInt()
        val chunkZ = buf.readInt()
        val fullChunk = buf.readBoolean()
        val primaryBitMask = buf.readVarInt()
        val tag = buf.readNbt()
        val size = buf.readVarInt()
        return ChunkDataPacket(
            chunkX = chunkX,
            chunkZ = chunkZ,
            fullChunk = fullChunk,
            availableSections = primaryBitMask,
            heightmaps = tag,
            size = size
        )
    }
}