package sx.rx

import rx.*
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.subscriber
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers
import java.time.Duration
import java.time.Period
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

    fun observeOn(observeOnFunction: () -> Scheduler) : Unit { observable = observable.observeOn(observeOnFunction()) }
    fun transform(onTransformFunction: (o: Observable<T>) -> Observable<T>) : Unit { observable = onTransformFunction(observable) }
    fun onCompleted(onCompletedFunction: () -> Unit) : Unit { subscriber = subscriber.onCompleted(onCompletedFunction) }
    fun onError(onErrorFunction: (t : Throwable) -> Unit) : Unit { subscriber = subscriber.onError(onErrorFunction) }
    fun onNext(onNextFunction: (t : T) -> Unit) : Unit { subscriber = subscriber.onNext(onNextFunction) }
    fun onStart(onStartFunction : () -> Unit) : Unit { subscriber = subscriber.onStart(onStartFunction) }
}

interface AwaitableSubscription : Subscription {
    fun await()
    fun await(timeout: Duration)
}

/**
 * Subscription which can be synchronized with completion
 * A shared/multicast Observable is created from the original and subscribed to on creation of this instance.
 * The class decorates a subscription to this shared Observable.
 * The await methods pass through to a lazily created Completable.
 */
class AwaitableSubscriptionImpl<T>(
        observable: Observable<T>,
        subscriber: Subscriber<T>): AwaitableSubscription {

    private val multicast: Observable<T>
    private val subscription: Subscription

    init {
        this.multicast = observable.share()
        this.subscription = this.multicast.subscribe(subscriber)
    }

    private val completable by lazy {
        this.multicast.toCompletable()
    }

    override fun unsubscribe() {
        this.subscription.unsubscribe()
    }

    override fun isUnsubscribed(): Boolean {
        return this.subscription.isUnsubscribed
    }

    override fun await() {
        this.completable.await()
    }

    override fun await(timeout: Duration) {
        this.completable.await(timeout.toMillis(), TimeUnit.MILLISECONDS)
    }
}

/**
 * Subscribe with a subscriber that is configured inside body
 */
inline fun <T> Observable<T>.subscribeAwaitableWith(body: TransformingFunctionSubscriberModifier<T>.() -> Unit): AwaitableSubscription {
    val modifier = TransformingFunctionSubscriberModifier(this)
    modifier.body()

    return AwaitableSubscriptionImpl(this, modifier.subscriber)
}