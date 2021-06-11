package top.limbang.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 * ### Block Change
 * ## Fired whenever a block is changed within the render distance.
 * Changing a block in a chunk that is not loaded is not a stable action. The Notchian client currently uses a shared empty chunk which is modified for all block changes in unloaded chunks; while in 1.9 this chunk never renders in older versions the changed block will appear in all copies of the empty chunk. Servers should avoid sending block changes in unloaded chunks and clients should ignore such packets.
 * - [blockPosition] Block Coordinates.
 * - [blockId] The new block state ID for the block as given in the global palette. See that section for more information.
 */
@Serializable
data class BlockChangePacket(
    val blockPosition: Position,
    val blockId: Int
) : Packet

class BlockChangeDecoder : PacketDecoder<BlockChangePacket> {
    override fun decoder(buf: ByteBuf): BlockChangePacket {
        TODO("客户端应忽略此包")
//        return BlockChangePacket(buf.readBlockChange(), buf.readVarInt())
    }
}