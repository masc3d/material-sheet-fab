package sx.mq.mqtt

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttException.REASON_CODE_CLIENT_CONNECTED
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.Stopwatch
import sx.rx.limit
import sx.rx.retryWithExponentialBackoff
import sx.rx.toHotCache
import java.lang.Thread.MIN_PRIORITY
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.properties.Delegates

/**
 * MQTT dispatcher
 * Micro  broker which handles connectivity and persistence transparently.
 *
 * Created by masc on 19.05.17.
 */
class MqttDispatcher(
        private val client: MqttRxClient,
        private val persistence: IMqttPersistence,
        private val executorService: ExecutorService
) : IMqttRxClient {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val lock = ReentrantLock()

    /** Statistics update event. Provides a map of topic -> count */
    val statisticsUpdateEvent by lazy { this.statisticsUpdatEventSubject.hide() }
    private val statisticsUpdatEventSubject by lazy { BehaviorSubject.create<Map<String, Int>>() }

    /** Scheduler for general use */
    private val scheduler by lazy {
        Schedulers.from(this.executorService)
    }

    /** Specific dequeue scheduler, single-threaded, low priority */
    private val dequeuScheduler by lazy {
        Schedulers.from(Executors.newFixedThreadPool(1, {
            Thread(it).also {
                it.name = "mqtt-dispatcher-${it.id}"
                it.priority = MIN_PRIORITY
            }
        }))
    }

    /** Statistics/message count cache by topic name */
    private var statistics by Delegates.observable<MutableMap<String, Int>>(
            initialValue = mutableMapOf(),
            onChange = { _, _, v ->
                // Mainly for intiail event
                this.statisticsUpdatEventSubject.onNext(v.toMap())
            })

    init {
        // Get current statistics initially from persistence layer
        this.statistics = this.persistence.count().toMutableMap()

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
     * Internal helper for maintaining statistics.
     * Updates counter cache and emits event.
     * @param topicName Topic name
     * @param messageAmountAdded Amount of messages added. This can be a negative value when messages have been removed
     */
    private fun updateStatistics(topicName: String, messageAmountAdded: Int) {
        val count = this.statistics.getOrPut(topicName, { 0 })
        this.statistics.put(topicName, count + messageAmountAdded)

        // Emit a copy of the statistics map
        this.statisticsUpdatEventSubject.onNext(this.statistics.toMap())
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
                // Backpressure trigger events as each message publish emits)
                .debounce(1, TimeUnit.SECONDS)
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
                            .concatMap { m ->
                                // Map each persisted message to publish/remove flow (sequentially)
                                this.client
                                        .publish(m.topicName, m.toMqttMessage())
                                        .doOnSubscribe {
                                            log.trace("Publishing [m${m.persistentId}]")
                                        }
                                        .concatWith(
                                                Completable.fromAction {
                                                    // Remove from persistence when publish was successful
                                                    this.persistence.remove(m)

                                                    this.updateStatistics(
                                                            topicName = m.topicName,
                                                            messageAmountAdded = -1
                                                    )

                                                    log.trace("Removed [m${m.persistentId}]")
                                                }
                                                        .subscribeOn(this@MqttDispatcher.dequeuScheduler)
                                        )
                                        .toSingleDefault(m)
                                        .subscribeOn(this@MqttDispatcher.dequeuScheduler)
                                        .blockingGet()

                                Observable.just(m)
                            }
                            .doOnComplete {
                                if (count > 0)
                                    log.info("Dequeued ${count} in [${sw}]")
                            }
                            .subscribeOn(scheduler)
                            // Map processed batch back to trigger unit
                            .ignoreElements()
                            .toSingleDefault(trigger)
                            .toObservable()
                }
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

            this.updateStatistics(
                    topicName = topicName,
                    messageAmountAdded = 1)

            if (this.isConnected) {
                // Trigger dequeue flow
                this.dequeueTrigger.onNext(Unit)
            }
        }
                .toHotCache(scheduler)
    }

    /**
     * Subscribe to topic
     * @param topicName Topic name
     * @param qos Qos
     */
    override fun subscribe(topicName: String, qos: Int): Observable<MqttMessage> {
        // TODO: replace passthrough with durable subscription. consumers shouldn't have to worry.
        return this.client.subscribe(
                topicName = topicName,
                qos = qos
        )
    }

    /**
     * Unsubscribe from topic
     * @param topicName Topic name
     */
    override fun unsubscribe(topicName: String): Completable {
        return this.client.unsubscribe(
                topicName
        )
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
                        log.info("Attempting connection to ${this.client.uri}")
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