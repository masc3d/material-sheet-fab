package org.deku.leoz.mobile.rx

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.ui.core.Activity
import org.slf4j.Logger
import sx.android.isConnectivityProblem
import sx.android.rx.observeOnMainThread
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

/**
 * Extension method for easily binding observable lifecycle to activity progress indicator
 */
fun <T> Observable<T>.composeWithActivityProgress(activity: Activity): Observable<T> {
    return this
            .doOnSubscribe {
                activity.progressIndicator.show()
            }
            .doFinally {
                activity.progressIndicator.hide()
            }
}

fun <T> Observable<T>.composeAsRest(activity: Activity, @StringRes errorMessage: Int = 0): Observable<T> {
    return this
            .observeOnMainThread()
            .composeWithActivityProgress(activity)
            .doOnError {
                if (errorMessage != 0) {
                    activity.snackbarBuilder
                            .message(
                                    if (it.isConnectivityProblem)
                                        R.string.error_connectivity
                                    else
                                        errorMessage
                            )
                            .duration(Snackbar.LENGTH_LONG)
                            .build().show()
                }
            }
}