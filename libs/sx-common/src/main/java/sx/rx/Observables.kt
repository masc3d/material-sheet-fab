package sx.rx

import io.reactivex.*
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import sx.time.Duration
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

fun <T> T.toSingletonObservable(): Observable<T> = Observable.just(this)
fun <T> Throwable.toObservable(): Observable<T> = Observable.error(this)

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
 */
fun <T> ConnectableObservable<T>.connected(): Observable<T> {
    this.connect()
    return this
}

/**
 * Transforms observable into a hot completable
 * Created by masc on 23/06/16.
 */
fun <T> Observable<T>.toHotCompletable(executor: Executor? = null): Completable {
    return this.subscribeOn(executor).publish().connected().ignoreElements()
}

/**
 * Transform Observable into a hot one with replay applied
 */
fun <T> Observable<T>.toHotReplay(executor: Executor? = null): Observable<T> {
    return this.subscribeOn(executor).replay().connected()
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
 * Transform Observable into a hot one with replay applied
 */
fun <T> Observable<T>.toHotReplay(scheduler: Scheduler? = null): Observable<T> {
    return (if (scheduler != null) this.subscribeOn(scheduler) else this)
            .replay()
            .connected()
}

fun <T> Observable<T>.toHotReplay(): Observable<T> {
    return this.toHotReplay(scheduler = null)
}

/**
 * Retry with specific count, action handler and timer provider
 * @param count Retry count
 * @param action Callback invoked for every retry/error, returning a (timer) Observable
 */
fun <T> Observable<T>.retryWith(
        // TODO: Int.MAX_VALUE doesn't seem to work for this implementation (freezes), while it works with Completable which is essentially the same implementation
        count: Int,
        action: (retry: Long, error: Throwable) -> Observable<Long> = { _, _ -> Observable.just(0) })
        : Observable<T> {

    return this.retryWhen { attempts ->
        attempts.zipWith(Observable.rangeLong(1, count.toLong() + 1), BiFunction { n: Throwable, i: Long ->
            Pair(n, i)
        }).flatMap { p ->
            val error = p.first
            val retryCount = p.second

            if (retryCount <= count) {
                return@flatMap action(retryCount, error)
            } else {
                return@flatMap Observable.error<Throwable>(error)
            }
        }
    }
}

/**
 * Retry with specific count, action handler and timer provider
 * @param count Retry count
 * @param action Callback invoked for every retry/error, returning a (timer) Observable
 */
fun Completable.retryWith(
        count: Int,
        action: (retry: Long, error: Throwable) -> Flowable<Long> = { _, _ -> Flowable.just(0) })
        : Completable {

    return this.retryWhen { attempts ->
        attempts.zipWith(Flowable.rangeLong(1, count.toLong() + 1), BiFunction { n: Throwable, i: Long ->
            Pair(n, i)
        }).flatMap { p ->
            val error = p.first
            val retryCount = p.second

            if (retryCount <= count) {
                return@flatMap action(retryCount, error)
            } else {
                return@flatMap Flowable.error<Throwable>(error)
            }
        }
    }
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
        count: Int = Int.MAX_VALUE,
        initialDelay: Duration,
        maximumDelay: Duration,
        exponentialBackoff: Double = 2.0,
        action: (retry: Long, delay: Duration, error: Throwable) -> Unit = { _, _, _ -> }): Completable {
    return this.retryWith(
            count = count,
            action = { retry: Long, error: Throwable ->
                var delay = initialDelay.times(Math.pow(
                        exponentialBackoff,
                        retry.toDouble()).toLong())

                if (delay > maximumDelay) {
                    delay = maximumDelay
                }

                action(retry, delay, error)

                Flowable.timer(delay.toMillis(), TimeUnit.MILLISECONDS)
            }
    )
}


