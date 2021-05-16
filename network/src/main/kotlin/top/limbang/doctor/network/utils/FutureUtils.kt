package top.limbang.doctor.network.utils

import io.netty.util.concurrent.Future
import io.netty.util.concurrent.ImmediateEventExecutor

/**
 *
 * @author WarmthDawn
 * @since 2021-05-16
 */

object FutureUtils {
    fun <T> success(result: T): Future<T> {
        return ImmediateEventExecutor::INSTANCE.get().newSucceededFuture(result)
    }

    fun <T> failure(cause: Throwable): Future<T> {
        return ImmediateEventExecutor::INSTANCE.get().newFailedFuture(cause)
    }

    fun pass(): Future<Unit> {
        return success(Unit)
    }
}