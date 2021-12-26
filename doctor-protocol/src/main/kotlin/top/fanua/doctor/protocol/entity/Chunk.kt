package top.fanua.doctor.protocol.entity

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.extension.readVarInt
import kotlin.math.abs


/**
 *
 * @author Doctor_Yin
 * @since 2021/12/25:15:55
 */
@Serializable
data class Chunk(
    val chunkX: Int,
    val chunkZ: Int,
    val section: Map<Int, Section?>
)

@Serializable
data class Section(
    val yBase: Int,
    val blocks: BlockStorage,
    val blockLight: NibbleArray3d,
    val skyLight: NibbleArray3d
)

@Serializable
data class BlockStorage(
    val bitsPerEntry: Int,
    val states: List<BlockState>,
    val flexibleStorage: FlexibleStorage
) {
    private val air = BlockState(0, 0)

    constructor(bitsPerEntry: Int, buf: ByteBuf) : this(
        bitsPerEntry,
        getBlockStates(buf),
        flexibleStorage = FlexibleStorage(bitsPerEntry, readLongArray(buf))
    )

    operator fun get(x: Int, y: Int, z: Int): BlockState {
        val id: Int = flexibleStorage.get(abs(index(x, y, z)))
        return if (bitsPerEntry <= 8) if (id >= 0 && id < states.size) states[id] else air else rawToState(id)
    }

}

private fun readLongArray(buf: ByteBuf): LongArray {
    val maxLength = buf.readableBytes() / 8
    val i: Int = buf.readVarInt()
    val array = LongArray(i)
    if (array.size != i && i > maxLength) throw DecoderException("LongArray with size $i is bigger than allowed $maxLength")
    for (j in array.indices) {
        array[j] = buf.readLong()
    }
    return array
}

private fun rawToState(raw: Int): BlockState {
    return BlockState(raw shr 4, raw and 0xF)
}

fun index(x: Int, y: Int, z: Int): Int {
    return y shl 8 or (z shl 4) or x
}

private fun getBlockStates(buf: ByteBuf): List<BlockState> {
    val size = buf.readVarInt()
    val list = mutableListOf<BlockState>()
    for (i in 0 until size) {
        val rawId: Int = buf.readVarInt()
        list.add(BlockState(rawId shr 4, rawId and 0xF))
    }
    return list
}

@Serializable
data class BlockState(
    val id: Int,
    val data: Int
)

@Serializable
@Suppress("ArrayInDataClass")
data class FlexibleStorage(
    val data: LongArray,
    val bitsPerEntry: Int,
    val size: Int,
    val maxEntryValue: Long
) {
    fun get(index: Int): Int {
        if (index < 0 || index > size - 1) {
            throw IndexOutOfBoundsException()
        }
        val bitIndex = index * bitsPerEntry
        val startIndex = bitIndex / 64
        val endIndex = ((index + 1) * bitsPerEntry - 1) / 64
        val startBitSubIndex = bitIndex % 64
        return if (startIndex == endIndex) {
            (data[startIndex] ushr startBitSubIndex and maxEntryValue).toInt()
        } else {
            val endBitSubIndex = 64 - startBitSubIndex
            (data[startIndex] ushr startBitSubIndex or data[endIndex] shl endBitSubIndex and maxEntryValue).toInt()
        }
    }

    constructor(bitsPerEntry: Int, data: LongArray) : this(
        bitsPerEntry =
        if (bitsPerEntry < 1 || bitsPerEntry > 32) {
            throw  IllegalArgumentException("BitsPerEntry cannot be outside of accepted range.")
        } else bitsPerEntry,
        data = data,
        size = data.size * 64 / bitsPerEntry,
        maxEntryValue = (1L shl bitsPerEntry) - 1
    )
}

@Suppress("ArrayInDataClass")
@Serializable
data class NibbleArray3d(val data: ByteArray) {
    constructor(buf: ByteBuf) : this(readByteArray(buf))
}

private fun readByteArray(buf: ByteBuf): ByteArray {
    val byteArray = ByteArray(2048)
    buf.readBytes(byteArray)
    return byteArray
}
