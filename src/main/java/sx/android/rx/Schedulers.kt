package sx.android.rx

import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
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

fun <T, E> Observable<T>.observeOnMainThreadUntilEvent(provider: LifecycleProvider<E>, event: E): Observable<T> {
    return this
            .observeOnMainThread()
            .bindUntilEvent(provider, event)
}

fun <E> Completable.observeOnMainThreadUntilEvent(provider: LifecycleProvider<E>, event: E): Completable {
    return this
            .observeOnMainThread()
            .bindUntilEvent(provider, event)
}

fun <T, E> Maybe<T>.observeOnMainThreadUntilEvent(provider: LifecycleProvider<E>, event: E): Maybe<T> {
    return this
            .observeOnMainThread()
            .bindUntilEvent(provider, event)
}

fun <T, E> Single<T>.observeOnMainThreadUntilEvent(provider: LifecycleProvider<E>, event: E): Single<T> {
    return this
            .observeOnMainThread()
            .bindUntilEvent(provider, event)
}

fun <T, E> Observable<T>.observeOnMainThreadWithLifecycle(provider: LifecycleProvider<E>): Observable<T> {
    return this
            .observeOnMainThread()
            .bindToLifecycle(provider)
}

fun <E> Completable.observeOnMainThreadWithLifecycle(provider: LifecycleProvider<E>): Completable{
    return this
            .observeOnMainThread()
            .bindToLifecycle(provider)
}

fun <T, E> Maybe<T>.observeOnMainThreadWithLifecycle(provider: LifecycleProvider<E>): Maybe<T> {
    return this
            .observeOnMainThread()
            .bindToLifecycle(provider)
}

fun <T, E> Single<T>.observeOnMainThreadWithLifecycle(provider: LifecycleProvider<E>): Single<T> {
    return this
            .observeOnMainThread()
            .bindToLifecycle(provider)
}

