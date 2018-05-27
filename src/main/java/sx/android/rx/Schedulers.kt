package sx.android.rx

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers


// Android scheduler helpers

fun <T> Observable<T>.observeOnMainThread(): Observable<T> =
        this.observeOn(AndroidSchedulers.mainThread())

fun Completable.observeOnMainThread(): Completable
        = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.observeOnMainThread(): Maybe<T> =
        this.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.observeOnMainThread(): Single<T> =
        this.observeOn(AndroidSchedulers.mainThread())