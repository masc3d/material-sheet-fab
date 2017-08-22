package org.deku.leoz.mobile.rx

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.slf4j.Logger
import sx.rx.toHotReplay

/**
 * Transforms obsevable into tailored hot observable for IO operations
 * which subscribes on IO scheduler, observes on main android thread.
 * @param log Optional logger for logging errors
 */
fun <T> Observable<T>.toHotIoObservable(log: Logger? = null): Observable<T> {
    return this
            .subscribeOn(Schedulers.io())
            .doOnError {
                log?.error(it.message)
            }
            .toHotReplay()
}