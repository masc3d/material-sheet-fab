package sx.mq.mqtt

import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.flatMapSequence
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toSingle
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttException.REASON_CODE_CLIENT_CONNECTED
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish
import org.slf4j.LoggerFactory
import sx.Stopwatch
import sx.rx.*
import sx.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * MQTT dispatcher
 * Micro  broker which handles connectivity and persistence transparently.
 *
 * Created by masc on 19.05.17.
 */
class MqttDispatcher(
        private val client: MqttRxClient,
        private val persistence: IMqttPersistence,
        private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
) : IMqttRxClient {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val lock = ReentrantLock()

    init {
        this.client.statusEvent.subscribe { it ->
            when (it) {
                is MqttRxClient.Status.ConnectionLost -> {
                    log.warn("Connection lost [${it.cause?.message ?: "-"}]")
                    this.dequeueSubscription = null
                    this@MqttDispatcher.connect()
                }

                is MqttRxClient.Status.ConnectionComplete -> {
                    log.info("Connection established")
                    this.dequeue()
                }
            }
        }
    }

    /**
     * The current connection subscription.
     * Setting this property to null will cancel/dispose the subscription
     */
    private var connectionSubscription: Disposable? = null
        set(value) {
            // Dispose the previous connection subscription
            field?.dispose()
            field = value
        }

    /**
     * Dequeue trigger subject
     */
    private val dequeueTrigger = BehaviorSubject.createDefault<Unit>(Unit).toSerialized()
    /**
     * The current dequeue subscription.
     * Setting this property to null will cancel/dispose the subscription
     */
    private var dequeueSubscription: Disposable? = null
        set(value) {
            this.lock.withLock {
                field?.dispose()
                field = value
            }
        }

    init {
        this.dequeue()
    }

    private fun dequeue() {
        val sw = Stopwatch.createUnstarted()
        var count: Int = 0

        // This observable never completes, as it's subject based.
        this.dequeueSubscription = this.dequeueTrigger
                // Backpressure trigger events as each message publish emits
                .toFlowable(BackpressureStrategy.LATEST)
                .toObservable()
                .concatMap { trigger ->
                    log.trace("Starting dequeue flow")
                    this.persistence.get()
                            .doOnSubscribe {
                                count = 0
                                sw.reset(); sw.start()
                            }
                            .doOnNext {
                                count++
                            }
                            .concatMap {
                                // Map each persisted message to publish/remove flow (sequentially)
                                log.trace("Publishing [m${it.persistentId}]")
                                this.client
                                        .publish(it.topicName, it.toMqttMessage())
                                        .concatWith(Completable.fromAction {
                                            // Remove from persistence when publish was successful
                                            this.persistence.remove(it)
                                            log.trace("Removed [m${it.persistentId}]")
                                        })
                                        .toSingleDefault(it)
                                        .toObservable()
                            }
                            .doOnComplete {
                                if (count > 0)
                                    log.info("Dequeued ${count} in [${sw}]")
                            }
                            // Map processed batch back to trigger unit
                            .ignoreElements()
                            .toSingleDefault(trigger)
                            .toObservable()
                }
                .subscribeOn(executorService)
                .subscribeBy(onError = {
                    log.error("Dequeue terminated with error [${it.message}]")
                })
    }

    /**
     * Publish to topic
     * @param topicName Topic name
     * @param message Message to publish
     */
    override fun publish(topicName: String, message: MqttMessage): Completable {
        return Completable.fromAction {
            // Store message persistently
            this.persistence.add(
                    topicName = topicName,
                    message = message)

            if (this.isConnected) {
                // Trigger dequeue flow
                this.dequeueTrigger.onNext(Unit)
            }
        }
                .toHotCache(this.executorService)
    }

    override fun subscribe(topicName: String, qos: Int): Observable<MqttMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Disconnect from remote broker and discontinue connection retries.
     * The returned {@link Completable} will always be completed without error.
     */
    override fun disconnect(): Completable {
        this.lock.withLock {
            log.info("Disconnecting")
            this.connectionSubscription = null
            return this.client.disconnect()
        }
    }

    override fun unsubscribe(topicName: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Start remote broker connection.
     * The connection will be retried internally until it succeeds or {@link disconnect} is called.
     * The returned {@link Completable} will always be completed without error.
     */
    override fun connect(): Completable {
        this.lock.withLock {
            log.info("Starting connection cycle")
            // RxClient observables are hot, thus need to defer in order to re-subscribe properly on retry
            this.connectionSubscription = Completable
                    .defer {
                        this.client.connect()
                    }
                    .onErrorComplete {
                        when (it) {
                        // Avoid retries when already connected
                            is MqttException -> it.reasonCode == REASON_CODE_CLIENT_CONNECTED.toInt()
                            else -> false
                        }
                    }
                    .retryWithExponentialBackoff(
                            initialDelay = Duration.ofSeconds(2),
                            maximumDelay = Duration.ofMinutes(2),
                            action = { retry, delay, error ->
                                log.error("Connection failed. [${error.message}] Retry [${retry}] in ${delay}")
                            })
                    .subscribe()

            return Completable.complete()
        }
    }

    /**
     * Indicates connection status
     */
    val isConnected: Boolean
        get() = this.client.isConnected
}