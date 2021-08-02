package top.fanua.doctor.protocol.entity.math

import kotlinx.serialization.Serializable
import kotlin.math.floor
import kotlin.math.sqrt

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
@Serializable
open class Vec3i(
    val x: Int,
    val y: Int,
    val z: Int
) : Comparable<Vec3i> {
    companion object {
        val NULL_VECTOR = Vec3i(0, 0, 0)
    }

    constructor(x: Double, y: Double, z: Double)
            : this(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())

    override fun compareTo(other: Vec3i): Int {
        return if (this.y == other.y) {
            if (this.z == other.z)
                this.x - other.x
            else this.z - other.z
        } else {
            this.y - other.y
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec3i) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    override fun toString(): String {
        return "Vec3i(x=$x, y=$y, z=$z)"
    }


}



/**
 * 向量叉乘
 */
fun Vec3i.crossProduct(vec: Vec3i): Vec3i {
    return Vec3i(
        this.y * vec.z - this.z * vec.y,
        this.z * vec.x - this.x * vec.z,
        this.x * vec.y - this.y * vec.x
    )
}


/**
 * 距离
 */
fun Vec3i.getDistance(xIn: Int, yIn: Int, zIn: Int): Double {
    val d0 = (this.x - xIn).toDouble()
    val d1 = (this.y - yIn).toDouble()
    val d2 = (this.z - zIn).toDouble()
    return sqrt(d0 * d0 + d1 * d1 + d2 * d2)
}

/**
 * 两个Vec3i的距离平方
 */
fun Vec3i.distanceSq(toX: Double, toY: Double, toZ: Double): Double {
    val d0 = this.x - toX
    val d1 = this.y - toY
    val d2 = this.z - toZ
    return d0 * d0 + d1 * d1 + d2 * d2
}

/**
 * 距离另一个Vec3i中心距离频繁
 */
fun Vec3i.distanceSqToCenter(xIn: Double, yIn: Double, zIn: Double): Double {
    val d0 = this.x + 0.5 - xIn
    val d1 = this.y + 0.5 - yIn
    val d2 = this.z + 0.5 - zIn
    return d0 * d0 + d1 * d1 + d2 * d2
}

/**
 * 两个Vec3i的距离平方
 */
fun Vec3i.distanceSq(to: Vec3i): Double = this.distanceSq(to.x.toDouble(), to.y.toDouble(), to.z.toDouble())
