package top.fanua.doctor.translation.api

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
interface IResources {
    val loaded: Boolean

    operator fun get(key: String): String
    operator fun contains(key: String): Boolean
    fun getList(start: String): Map<String, String>
    fun startWith(start: String): Boolean

    fun load()
}
