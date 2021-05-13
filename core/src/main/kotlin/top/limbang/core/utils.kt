package top.limbang.minecraft.api

/**
 *
 * @author WarmthDawn
 * @since 2021-05-13
 */

/**
 * 某个讨厌泛型警告的强制转换
 */
fun <T: Any> Any.cast() : T {
    return this as T
}