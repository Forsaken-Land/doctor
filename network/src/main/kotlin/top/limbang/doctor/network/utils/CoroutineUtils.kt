package top.limbang.doctor.network.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.util.concurrent.Future
import top.limbang.doctor.core.cast
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */


suspend fun <T> Future<T>.suspendRun() {
    return suspendCoroutine { cont ->
        this.addListener {
            if (it.isSuccess) {
                cont.resume(Unit)
            } else {
                cont.resumeWithException(it.cause())
            }
        }
    }
}