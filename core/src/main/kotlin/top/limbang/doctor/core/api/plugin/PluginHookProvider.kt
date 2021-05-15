package top.limbang.doctor.core.api.plugin

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
typealias Hook<T> = T.() -> Unit

interface PluginHookProvider<T> {
    /**
     * 添加钩子
     * [hook] 一个lambda表达式，表示对钩子提供的类型进行操作
     */
    fun addHook(hook: Hook<T>)

    /**
     * 删除钩子
     */
    fun delHook(hook: Hook<T>)
}