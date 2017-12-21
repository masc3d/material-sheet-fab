package sx.rx

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

fun <T> T.toSingletonObservable(): Observable<T> = Observable.just(this)
fun <T> Throwable.toObservable(): Observable<T> = Observable.error(this)

interface CompositeDisposableSupplier : Disposable {
    val compositeDisposable: CompositeDisposable

    override fun isDisposed(): Boolean {
        return this.compositeDisposable.isDisposed
    }

    override fun dispose() {
        this.compositeDisposable.dispose()
    }
}

/**
 * Subscribe on a specific executor
 * @param executor Executor to subscribe on.
 */
fun <T> Observable<T>.subscribeOn(executor: Executor? = null): Observable<T> {
    return if (executor != null) this.subscribeOn(Schedulers.from(executor)) else this
}

/**
 * Subscribe on a specific sexecutor
 * @param executor Executor to subscribe on.
 */
fun Completable.subscribeOn(executor: Executor? = null): Completable {
    return if (executor != null) this.subscribeOn(Schedulers.from(executor)) else this
}

/**
 * Observe on specific executor
 */
fun <T> Observable<T>.observeOn(executor: Executor? = null): Observable<T> {
    return if (executor != null) this.observeOn(Schedulers.from(executor)) else this
}

/**
 * Connects to observable to force start emitting and returns the Observable (instead of the subscription
 * as .connect() does.
 * @param composite The composite disposable to attach to for disposal
 */
fun <T> ConnectableObservable<T>.connected(composite: CompositeDisposable? = null): Observable<T> {
    val disposable = this.connect()
    composite?.add(disposable)
    return this
}

/**
 * Transforms observable into a hot completable
 * Created by masc on 23/06/16.
 */
fun <T> Observable<T>.toHotCompletable(composite: CompositeDisposable? = null, executor: Executor? = null): Completable {
    return this.subscribeOn(executor).publish().connected(composite).ignoreElements()
}

/**
 * Transform Observable into a hot one with replay applied
 */
fun <T> Observable<T>.toHotReplay(composite: CompositeDisposable? = null, executor: Executor? = null): Observable<T> {
    return this.subscribeOn(executor).replay().connected(composite)
}

/**
 * Transforms Completable into a hot one with cache applied
 */
fun Completable.toHotCache(executor: Executor? = null, onError: (Throwable) -> Unit = {}): Completable {
    val c = this.subscribeOn(executor)
            .cache()

    c.subscribeBy(onError = onError)

    return c
}

/**
 * Transforms Completable into a hot one with cache applied
 */
fun Completable.toHotCache(scheduler: Scheduler, onError: (Throwable) -> Unit = {}): Completable {
    val c = this.subscribeOn(scheduler)
            .cache()

    c.subscribeBy(onError = onError)

    return c
}

/**
 * Transforms Completable into a hot one with cache applied
 */
fun Completable.toHotCache(onError: (Throwable) -> Unit = {}): Completable {
    val c = this.subscribeOn(executor = null)
            .cache()

    c.subscribeBy(onError = onError)

    return c
}

/**
 * Transform Observable into a hot one with replay applied
 */
fun <T> Observable<T>.toHotReplay(composite: CompositeDisposable? = null, scheduler: Scheduler? = null): Observable<T> {
    return (if (scheduler != null) this.subscribeOn(scheduler) else this)
            .replay()
            .connected(composite)
}

fun <T> Observable<T>.toHotReplay(composite: CompositeDisposable? = null): Observable<T> =
        this.toHotReplay(composite = composite, scheduler = null)

fun <T> Observable<T>.behave(composite: CompositeDisposable): Observable<T> =
        this.replay(1).connected(composite)

fun <T> Observable<T>.behave(supplier: CompositeDisposableSupplier? = null): Observable<T> =
        this.replay(1).connected(supplier?.compositeDisposable)

/**
 * Retry with specific count, action handler and timer provider
 * @param count Retry count
 * @param action Callback invoked for every retry/error, returning a (timer) Observable
 */
fun <T> Observable<T>.retryWith(
        // TODO: Int.MAX_VALUE doesn't seem to work for this implementation (freezes), while it works with Completable which is essentially the same implementation
        count: Short,
        action: (retry: Long, error: Throwable) -> Observable<Long> = { _, _ -> Observable.just(0) })
        : Observable<T> {

    return this.retryWhen { attempts ->
        attempts.zipWith(Observable.rangeLong(1, count.toLong() + 1), BiFunction { n: Throwable, i: Long ->
            Pair(n, i)
        }).flatMap { p ->
            val error = p.first
            val retryCount = p.second

            if (retryCount <= count) {
                try {
                    return@flatMap action(retryCount, error)
                } catch(e: Throwable) {
                    return@flatMap Observable.error<Throwable>(error)
                }
            } else {
                return@flatMap Observable.error<Throwable>(error)
            }
        }
    }
}

/**
 * Bind disposable to a composite disposable
 */
fun <T : Disposable> T.bind(supplier: CompositeDisposableSupplier): T {
    supplier.compositeDisposable.add(this)
    return this
}

/**
 * Retry with specific count, action handler and timer provider
 * @param count Retry count
 * @param action Callback invoked for every retry/error, returning a (timer) Observable
 */
fun Completable.retryWith(
        count: Short,
        action: (retry: Long, error: Throwable) -> Observable<Long> = { _, _ -> Observable.just(0) })
        : Completable {

    return this
            .toObservable<Any>()
            .retryWith(
                    count = count,
                    action = action
            )
            .ignoreElements()
}

/**
 * Retry with exponential backoff
 * @param count Retry count
 * @param initialDelay Initial delay
 * @param maximumDelay Maximum delay
 * @param exponentialBackoff Exponential backoff factor. Defaults to 2.0.
 * @param action Action on retry
 */
fun Completable.retryWithExponentialBackoff(
        count: Short = Short.MAX_VALUE,
        initialDelay: Duration,
        maximumDelay: Duration,
        exponentialBackoff: Double = 2.0,
        action: (retry: Long, delay: Duration, error: Throwable) -> Unit = { _, _, _ -> }): Completable {
    return this.retryWith(
            count = count,
            action = { retry: Long, error: Throwable ->

                var delay = try {
                    initialDelay.multipliedBy(
                            Math.pow(
                                    exponentialBackoff,
                                    retry.toDouble()
                            ).toLong()
                    )
                } catch (e: Exception) {
                    // Overflow: revert to maximum delay
                    maximumDelay
                }

                if (delay > maximumDelay) {
                    delay = maximumDelay
                }

                action(retry, delay, error)

                Observable.timer(delay.toMillis(), TimeUnit.MILLISECONDS)
            }
    )
}


