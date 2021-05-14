package top.limbang.doctor.core.api.plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
typealias Hook<T> = T.() -> Boolean

interface PluginHookProvider<T> {
    fun addHook(hook: Hook<T>)
    fun delHook(hook: Hook<T>)
}