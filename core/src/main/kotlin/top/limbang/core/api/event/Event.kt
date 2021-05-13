package top.limbang.core.api.event

/**
 * 任何实现此接口的类均可以视为一个事件
 * 推荐使用object类或者枚举
 * 如果使用普通类对象记得重写equals和hashcode方法
 *
 * @param T 事件的返回值类型
 * @author WarmthDawn
 * @since 2021-05-13
 */
interface Event<T>
