package sx.rx

import org.slf4j.LoggerFactory
import io.reactivex.*
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.ArrayList
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

fun <T> T.toSingletonObservable() : Observable<T> = Observable.just(this)
fun <T> Throwable.toObservable() : Observable<T> = Observable.error(this)

/**
 * Subscribe on a specific executor
 * @param executor Executor to subscribe on.
 */
fun <T> Observable<T>.subscribeOn(executor: Executor? = null): Observable<T> {
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
 * Transform Observable into a hot one with replay applied
 */
fun <T> Observable<T>.toHotReplay(scheduler: Scheduler? = null): Observable<T> {
    return this.compose {
        if (scheduler != null) this.subscribeOn(scheduler) else this
    }
}

fun <T> Observable<T>.toHotReplay(): Observable<T> {
    return this.toHotReplay(scheduler = null)
}

// TODO: the following additions would require migration to rxjava2 (if they are still useful)
///**
// * Transforming function subscribe modifier
// * @param observable Observable
// * @param init FunctionSubscriber
// */
//class TransformingFunctionSubscriberModifier<T>(observable: Observable<T>, init: FunctionSubscriber<T> = subscriber()) {
//    var observable: Observable<T> = observable
//        private set
//    var subscriber: FunctionSubscriber<T> = init
//        private set
//
//    init {
//        this.observable = observable
//    }
//
//    fun observeOn(observeOnFunction: () -> Scheduler): Unit {
//        observable = observable.observeOn(observeOnFunction())
//    }
//
//    fun transform(onTransformFunction: (o: Observable<T>) -> Observable<T>): Unit {
//        observable = onTransformFunction(observable)
//    }
//
//    fun compose(onComposeFunction: () -> Observable.Transformer<T, T>): Unit {
//        observable = observable.compose(onComposeFunction())
//    }
//
//    fun onCompleted(onCompletedFunction: () -> Unit): Unit {
//        subscriber = subscriber.onCompleted(onCompletedFunction)
//    }
//
//    fun onError(onErrorFunction: (t: Throwable) -> Unit): Unit {
//        subscriber = subscriber.onError(onErrorFunction)
//    }
//
//    fun onNext(onNextFunction: (t: T) -> Unit): Unit {
//        subscriber = subscriber.onNext(onNextFunction)
//    }
//
//    fun onStart(onStartFunction: () -> Unit): Unit {
//        subscriber = subscriber.onStart(onStartFunction)
//    }
//}
//
///**
// * Specific subscription which can be awaited on or cancalled
// */
//interface Awaitable : Subscription {
//    fun await()
//    fun await(timeout: Long, unit: TimeUnit)
//    fun cancel()
//}

///**
// * Awaitable implementation
// */
//class AwaitableImpl<T>(
//        observable: Observable<T>,
//        subscriber: Subscriber<T>) : Awaitable {
//
//    private val log = LoggerFactory.getLogger(this.javaClass)
//
//    /**
//     * Publish subject used for injecting items ie. CancellationException
//     */
//    private val subject: Subject<T, T>
//    /**
//     * The final observable
//     */
//    private val observable: Observable<T>
//    /**
//     * Subscription
//     */
//    private val subscription: Subscription
//
//    init {
//        this.subject = PublishSubject.create<T>().synchronized()
//
//        // Merge subject with observable
//        this.observable = this.subject.mergeWith(
//                // Complete the subject when the Observable completes
//                observable
//                        .doOnCompleted {
//                            subject.onCompleted()
//                        })
//                // Cache items, so the Completable used for waiting seems the same sequence/completion
//                // REMARK/BUG: share() misbehaves, re-emits items when {@link Completable#await} is called
//                // after the Observable has completed (no matter when the Completable actually has been created)
//                .cache()
//
//        // Subscribe to it internally
//        this.subscription = this.observable.subscribe(subscriber)
//    }
//
//    /**
//     * Lazily created {@link rx.Completable} for awaiting completion
//     */
//    private val completable: Completable by lazy {
//        this.observable.toCompletable()
//    }
//
//    /**
//     * Wait for completion
//     */
//    override fun await() {
//        this.completable.await()
//    }
//
//    /**
//     * Wait for completion
//     * @param timeout Timeout
//     * @param unit Timeout unit
//     */
//    override fun await(timeout: Long, unit: TimeUnit) {
//        this.completable.await(timeout, unit)
//    }
//
//    /**
//     * Cancel observable
//     */
//    override fun cancel() {
//        this.subject.onError(CancellationException())
//    }
//
//    /**
//     * @see {@link rx.Subscription#isUnsubscribed}
//     */
//    override fun isUnsubscribed(): Boolean {
//        return this.subscription.isUnsubscribed
//    }
//
//    /**
//     * @see {@link rx.Subscription#unsubscribe}
//     */
//    override fun unsubscribe() {
//        this.subscription.unsubscribe()
//    }
//}

///**
// * Subscribe with a subscriber that is configured inside body
// */
//inline fun <T> Observable<T>.subscribeAwaitableWith(body: TransformingFunctionSubscriberModifier<T>.() -> Unit): Awaitable {
//    val modifier = TransformingFunctionSubscriberModifier(this)
//    modifier.body()
//
//    return AwaitableImpl(modifier.observable, modifier.subscriber)
//}

/**
 * Retry with specific count, action handler and timer provider
 * @param count Retry count
 * @param action Callback invoked for every retry/error, returning a (timer) Observable
 */
fun <T> Observable<T>.retryWith(
        count: Int,
        action: (retry: Int, error: Throwable) -> Observable<Long> = { _, _ -> Observable.just(0) })
        : Observable<T> {

    return this.retryWhen { attempts ->
        attempts.zipWith(Observable.range(1, count + 1), BiFunction { n: Throwable, i: Int ->
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

