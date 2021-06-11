package top.limbang.doctor.translation.api

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
interface IResources {
    val loaded: Boolean

    operator fun get(key: String): String
    operator fun contains(key: String): Boolean

    fun load()
}