package sx.rx

import rx.*
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.PublishSubject
import rx.lang.kotlin.subscriber
import rx.lang.kotlin.synchronized
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers
import rx.subjects.Subject
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

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
    val sub = this.connect()
    return this
}

/**
 * Transforms observable into a hot completable
 * Created by masc on 23/06/16.
 */
fun <T> Observable<T>.toHotCompletable(executor: Executor? = null): Completable {
    return this.subscribeOn(executor).publish().connected().toCompletable()
}

/**
 * Transform Observable into a hot one with replay applied
 */
fun <T> Observable<T>.toHotReplay(executor: Executor? = null): Observable<T> {
    return this.subscribeOn(executor).replay().connected()
}

/**
 * Transforming function subscribe modifier
 * @param observable Observable
 * @param init FunctionSubscriber
 */
class TransformingFunctionSubscriberModifier<T>(observable: Observable<T>, init: FunctionSubscriber<T> = subscriber()) {
    var observable: Observable<T> = observable
        private set
    var subscriber: FunctionSubscriber<T> = init
        private set

    init {
        this.observable = observable
    }

    fun observeOn(observeOnFunction: () -> Scheduler): Unit {
        observable = observable.observeOn(observeOnFunction())
    }

    fun transform(onTransformFunction: (o: Observable<T>) -> Observable<T>): Unit {
        observable = onTransformFunction(observable)
    }

    fun onCompleted(onCompletedFunction: () -> Unit): Unit {
        subscriber = subscriber.onCompleted(onCompletedFunction)
    }

    fun onError(onErrorFunction: (t: Throwable) -> Unit): Unit {
        subscriber = subscriber.onError(onErrorFunction)
    }

    fun onNext(onNextFunction: (t: T) -> Unit): Unit {
        subscriber = subscriber.onNext(onNextFunction)
    }

    fun onStart(onStartFunction: () -> Unit): Unit {
        subscriber = subscriber.onStart(onStartFunction)
    }
}

interface Awaitable {
    fun await()
    fun await(timeout: Long, unit: TimeUnit)
    fun cancel()
}

/**
 * Specific subscription which can only be awaited for or cancelled.
 */
class AwaitableImpl<T>(
        observable: Observable<T>,
        subscriber: Subscriber<T>) : Awaitable {
    /**
     * Publish subject used for injecting items ie. CancellationException
     */
    private val subject: Subject<T, T>
    /**
     * The final observable
     */
    private val observable: Observable<T>
    /**
     * Subscription
     */
    private val subscription: Subscription

    init {
        this.subject = PublishSubject<T>().synchronized()

        // Merge subject with observable
        this.observable = this.subject.mergeWith(
                // Complete the subject when the consumed Observable completes
                observable.doOnCompleted {
                    subject.onCompleted()
                })
                // Share it, so a subsequently created Completable works as expected
                .share()

        // Subscribe to it internally
        this.subscription = this.observable.subscribe(subscriber)
    }

    /**
     * Lazily created completable for awaiting completion
     */
    private val completable by lazy {
        this.observable.toCompletable()
    }

    /**
     * Wait for observable to complete
     */
    override fun await() {
        this.completable.await()
    }

    /**
     * Wait for observable to complete
     * @param timeout Timeout
     * @param unit Timeout unit
     */
    override fun await(timeout: Long, unit: TimeUnit) {
        this.completable.await(timeout, unit)
    }

    /**
     * Cancel observable
     */
    override fun cancel() {
        this.subject.onError(CancellationException())
    }
}

/**
 * Subscribe with a subscriber that is configured inside body
 */
inline fun <T> Observable<T>.subscribeAwaitableWith(body: TransformingFunctionSubscriberModifier<T>.() -> Unit): Awaitable {
    val modifier = TransformingFunctionSubscriberModifier(this)
    modifier.body()

    return AwaitableImpl(modifier.observable, modifier.subscriber)
}