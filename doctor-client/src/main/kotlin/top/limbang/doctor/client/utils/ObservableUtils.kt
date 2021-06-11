package top.limbang.doctor.client.utils

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.core.api.event.EventEmitter

/**
 *
 * @author WarmthDawn
 * @since 2021-05-17
 */

fun <T> EventEmitter.asObservable(event: Event<T>): Observable<T> {
    return Observable.create {
        val handler = { t: T ->
            it.onNext(t)
        }
        this.on(event, handler)
        it.setCancellable {
            this.remove(event, handler)
        }
    }
}

fun <T> EventEmitter.asSingle(event: Event<T>): Single<T> {
    return Single.create {
        val handler = { t: T ->
            it.onSuccess(t)
        }
        this.once(event, handler)
        it.setCancellable {
            this.remove(event, handler)
        }
    }
}