package top.fanua.doctor.protocol.entity

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.definition.play.client.Position
import top.fanua.doctor.protocol.extension.readVarInt
import kotlin.math.abs


/**
 *
 * @author Doctor_Yin
 * @since 2021/12/25:15:55
 */

@Serializable
data class World(
    val chunks: MutableMap<Pair<Int, Int>, Chunk> = mutableMapOf()
) {
    fun getOrSet(x: Int, y: Int, z: Int, blockState: BlockState? = null): BlockState {
        val chunkX = if (x / 16 >= 0) x / 16
        else (x / 16) - 1
        val chunkY = if (y / 16 >= 0) y / 16
        else (y / 16) - 1
        val chunkZ = if (z / 16 >= 0) z / 16
        else (z / 16) - 1
        val blocks = chunks[Pair(chunkX, chunkZ)]?.section?.get(chunkY)?.blocks
        return if (blockState != null) {
            blocks?.set(
                if (x > 0) abs(x % 16) else abs(x % 16) + 1,
                abs(y) % 16,
                if (z > 0) abs(z % 16) else abs(z % 16) + 1,
                blockState
            )
            blockState
        } else {
            blocks?.get(
                if (x > 0) abs(x % 16) else abs(x % 16) + 1,
                abs(y) % 16,
                if (z > 0) abs(z % 16) else abs(z % 16) + 1
            ) ?: BlockState(0, 0)
        }
    }

    fun set(position: Position, blockState: BlockState) = getOrSet(position.x, position.y, position.z, blockState)
}

@Serializable
@Suppress("ArrayInDataClass")
data class Chunk(
    val chunkX: Int,
    val chunkZ: Int,
    val section: MutableMap<Int, Section?>,
    val biomesArray: ByteArray?
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
    var bitsPerEntry: Int,
    val states: MutableList<BlockState>,
    var flexibleStorage: FlexibleStorage
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

    operator fun set(x: Int, y: Int, z: Int, state: BlockState) {
        var id = if (bitsPerEntry <= 8) states.indexOf(state) else stateToRaw(state)
        if (id == -1) {
            states.add(state)
            if (states.size > 1 shl bitsPerEntry) {
                bitsPerEntry++
                var oldStates: List<BlockState> = states
                if (bitsPerEntry > 8) {
                    oldStates = ArrayList(states)
                    states.clear()
                    bitsPerEntry = 13
                }
                val oldStorage: FlexibleStorage = flexibleStorage
                flexibleStorage = FlexibleStorage(bitsPerEntry, flexibleStorage.size)
                for (index in 0 until flexibleStorage.size) {
                    flexibleStorage.set(
                        index, if (bitsPerEntry <= 8) oldStorage.get(index) else stateToRaw(
                            oldStates[index]
                        )
                    )
                }
            }
            id = if (bitsPerEntry <= 8) states.indexOf(state) else stateToRaw(state)
        }
        flexibleStorage.set(index(x, y, z), id)
    }

}

private fun stateToRaw(state: BlockState): Int {
    return state.id shl 4 or (state.data and 0xF)
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

private fun getBlockStates(buf: ByteBuf): MutableList<BlockState> {
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

    fun set(index: Int, value: Int) {
        if (index < 0 || index > size - 1) {
            throw IndexOutOfBoundsException()
        }
        require(!(value < 0 || value > maxEntryValue)) { "Value cannot be outside of accepted range." }
        val bitIndex = index * bitsPerEntry
        val startIndex = bitIndex / 64
        val endIndex = ((index + 1) * bitsPerEntry - 1) / 64
        val startBitSubIndex = bitIndex % 64
        data[startIndex] =
            data[startIndex] and (maxEntryValue shl startBitSubIndex).inv() or (value.toLong() and maxEntryValue) shl startBitSubIndex
        if (startIndex != endIndex) {
            val endBitSubIndex = 64 - startBitSubIndex
            data[endIndex] =
                data[endIndex] ushr endBitSubIndex shl endBitSubIndex or (value.toLong() and maxEntryValue) shr endBitSubIndex
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

    constructor(bitsPerEntry: Int, size: Int) : this(
        bitsPerEntry, LongArray(roundToNearest(size * bitsPerEntry, 64) / 64)
    )
}

private fun roundToNearest(value: Int, roundTo: Int): Int {
    var copy = roundTo
    return if (copy == 0) {
        0
    } else if (value == 0) {
        copy
    } else {
        if (value < 0) {
            copy *= -1
        }
        val remainder = value % copy
        if (remainder != 0) value + copy - remainder else value
    }
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
