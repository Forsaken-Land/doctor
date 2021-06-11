package top.limbang.doctor.protocol.utils

/**
 *
 * @author WarmthDawn
 * @since 2021-05-22
 */
fun smallestEncompassingPowerOfTwo(value: Int): Int {
    var i = value - 1
    i = i or i shr 1
    i = i or i shr 2
    i = i or i shr 4
    i = i or i shr 8
    i = i or i shr 16
    return i + 1
}