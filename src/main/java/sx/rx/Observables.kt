package sx.rx

import rx.Completable
import rx.Observable
import rx.Scheduler
import rx.Subscription
import rx.lang.kotlin.FunctionSubscriber
import rx.lang.kotlin.subscriber
import rx.observables.ConnectableObservable
import rx.schedulers.Schedulers
import java.util.concurrent.Executor

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
 * Subscription which can be sxnchronizued with completion
 */
interface AwaitableSubscription : Subscription {
    fun await()
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

/**
 * Subscribe with a subscriber that is configured inside body
 */
fun <T> Observable<T>.subscribeAwaitableWith(body: TransformingFunctionSubscriberModifier<T>.() -> Unit): AwaitableSubscription {
    val modifier = TransformingFunctionSubscriberModifier(this, subscriber<T>())
    modifier.body()

    // Create shared/multicast observable
    val multicast = modifier.observable.share()
    
    val sub = multicast.subscribe(modifier.subscriber)

    return object : AwaitableSubscription, Subscription by sub {
        override fun await() {
            return multicast.toCompletable().await()
        }
    }
}