package top.limbang.doctor.protocol.entity.math

import kotlinx.serialization.Serializable
import kotlin.math.log2

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
@Serializable
class BlockPos : Vec3i {
    constructor(x: Int, y: Int, z: Int) : super(x, y, z)
    constructor(x: Double, y: Double, z: Double) : super(x, y, z)
    constructor(source: Vec3i) : super(source.x, source.y, source.z)

    companion object {
        val ORIGIN = BlockPos(0, 0, 0)
        private val NUM_X_BITS: Int = 1 + log2(30000000.0).toInt()
        private val NUM_Z_BITS = NUM_X_BITS
        private val NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS
        private val Y_SHIFT = 0 + NUM_Z_BITS
        private val X_SHIFT = Y_SHIFT + NUM_Y_BITS
        private val X_MASK = (1L shl NUM_X_BITS) - 1L
        private val Y_MASK = (1L shl NUM_Y_BITS) - 1L
        private val Z_MASK = (1L shl NUM_Z_BITS) - 1L

        fun fromLong(serialized: Long): BlockPos {
            val x = (serialized shl 64 - X_SHIFT - NUM_X_BITS shr 64 - NUM_X_BITS).toInt()
            val y = (serialized shl 64 - Y_SHIFT - NUM_Y_BITS shr 64 - NUM_Y_BITS).toInt()
            val z = (serialized shl 64 - NUM_Z_BITS shr 64 - NUM_Z_BITS).toInt()
            return BlockPos(x, y, z)
        }
    }

    fun toLong(): Long {
        return (this.x.toLong() and X_MASK shl X_SHIFT) or
                (this.y.toLong() and Y_MASK shl Y_SHIFT) or
                (this.z.toLong() and Z_MASK shl 0)
    }


    override fun toString(): String {
        return "BlockPos(x=$x, y=$y, z=$z)"
    }


}