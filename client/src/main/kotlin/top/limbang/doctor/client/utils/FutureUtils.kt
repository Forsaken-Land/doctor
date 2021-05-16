package top.limbang.doctor.client.utils

import io.netty.util.concurrent.GlobalEventExecutor
import io.netty.util.concurrent.Promise

/**
 *
 * @author WarmthDawn
 * @since 2021-05-16
 */


fun <T> newPromise(act: (Promise<T>) -> Unit) = GlobalEventExecutor.INSTANCE.newPromise<T>().apply(act)
