package top.limbang.doctor.protocol.definition.play.client

import kotlinx.serialization.Serializable
import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.entity.nbt.NbtBase


/**
 * @author Doctor_Yin
 * @date 2021/5/1
 * @time 23:00
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