package top.fanua.doctor.core.api.event

/**
 * 事件处理器
 * @author WarmthDawn
 * @since 2021-05-13
 */
fun interface EventHandler<in T> {
    fun handle(args: T)
}
