package sx.android.rx

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Observe on android main thread
 * Created by masc on 25.07.17.
 */
fun <T> Observable<T>.observeOnMainThread(): Observable<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}

fun Completable.observeOnMainThread(): Completable {
    return this.observeOn(AndroidSchedulers.mainThread())
}