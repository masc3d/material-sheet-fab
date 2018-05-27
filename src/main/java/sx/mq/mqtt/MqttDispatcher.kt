package sx.mq.mqtt

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttException.REASON_CODE_CLIENT_CONNECTED
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.Stopwatch
import sx.log.slf4j.debug
import sx.log.slf4j.info
import sx.log.slf4j.trace
import sx.rx.limit
import sx.rx.retryWithExponentialBackoff
import sx.rx.toHotCache
import java.util.concurrent.ExecutorService
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
) : IMqttRxClient, AutoCloseable {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val lock = ReentrantLock()
    private val statsLock = ReentrantLock()

    /** Statistics update event. Provides a map of topic -> count */
    val statisticsUpdateEvent by lazy { this.statisticsUpdatEventSubject.hide() }
    private val statisticsUpdatEventSubject by lazy { BehaviorSubject.create<Map<String, Int>>() }

    /** Scheduler for general use */
    private val scheduler by lazy {
        Schedulers.from(this.executorService)
    }

    /** Schedulers by topic for parallels processing */
    private var schedulers = mutableMapOf<String, Scheduler>()

    /**
     * Get scheduler by topic
     * @param topic Topic name
     */
    private fun scheduler(topic: String): Scheduler =
            synchronized(schedulers) {
                schedulers.getOrPut(topic, {
                    this.scheduler.limit(maxConcurrency = 1)
                })
            }

    /** Statistics/message count cache by topic name */
    private var statistics by Delegates.observable<MutableMap<String, Int>>(
            initialValue = mutableMapOf(),
            onChange = { _, _, v ->
                // Mainly for intiail event
                this.statisticsUpdatEventSubject.onNext(v.toMap())
            })

    /**
     * Internal helper for maintaining statistics.
     * Updates counter cache and emits event.
     * @param topicName Topic name
     * @param messageAmountAdded Amount of messages added. This can be a negative value when messages have been removed
     */
    private fun updateStatistics(topicName: String, messageAmountAdded: Int) {
        synchronized(this.statistics) {
            val count = this.statistics.getOrPut(topicName, { 0 })
            this.statistics.put(topicName, count + messageAmountAdded)

            // Emit a copy of the statistics map
            this.statisticsUpdatEventSubject.onNext(this.statistics.toMap())
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

    val compositeDisposable = CompositeDisposable()

    /**
     * Observable dequeue topic trigger, emitting topic names
     */
    private val dequeueTopicTriggerSubject = PublishSubject.create<String>()

    private val dequeueTopicTrigger = dequeueTopicTriggerSubject
            .hide()

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
        // Get current statistics initially from persistence layer
        this.statistics = this.persistence.count().toMutableMap()

        this.client.statusEvent.subscribe { it ->
            when (it) {
                is MqttRxClient.Status.ConnectionLost -> {
                    log.warn("Connection lost [${it.cause?.message ?: "-"}]")
                    this.reconnect()
                }

                is MqttRxClient.Status.ConnectionComplete -> {
                    log.info("Connection established")
                    this.trigger()
                }
            }
        }

        this.startDequeue()
        this.trigger()
    }

    /**
     * Publish observable persistent messages to wire
     */
    private fun Observable<MqttPersistentMessage>.publishToWire(): Observable<MqttPersistentMessage> {
        return this.concatMap { m ->
            // Map each persisted message to publish/remove flow (sequentially)
            this@MqttDispatcher.client
                    .publish(m.topicName, m.toMqttMessage())
                    .doOnSubscribe {
                        log.trace { "Publishing [${m.topicName}] [m${m.persistentId}]" }
                    }
                    .concatWith(
                            Completable.fromAction {
                                // Remove from persistence when publish was successful
                                this@MqttDispatcher.persistence.remove(m)

                                this@MqttDispatcher.updateStatistics(
                                        topicName = m.topicName,
                                        messageAmountAdded = -1
                                )

                                log.trace { "Removed [m${m.persistentId}]" }
                            }
                    )
                    .blockingAwait()

            Observable.just(m)
        }
    }

    /**
     * Trigger dequeue
     * @param topicName Topic to trigger dequeue for. Omitting this parameter triggers all topics
     */
    private fun trigger(topicName: String? = null) {
        val topics = if (topicName != null)
        // Trigger specific topic
            listOf(topicName)
        else
        // Trigger all topics
            this.persistence.getTopics()

        topics.forEach {
            log.debug { "Triggering [${it}]" }
            this.dequeueTopicTriggerSubject.onNext(it)
        }
    }

    /**
     * Start dequeue subscription.
     * In case a subscription is already active, the old one will be disposed accordingly.
     */
    private fun startDequeue() {
        fun lfmt(topic: String, message: String) = "[${topic}] ${message}"

        // This observable never completes, as it's subject based.
        this.dequeueSubscription = this.dequeueTopicTrigger
                .doOnNext { log.debug { lfmt(it, "trigger") } }
                .groupBy { it }
                .flatMap { topicTrigger ->
                    val topic = topicTrigger.key ?: throw NoSuchElementException()

                    topicTrigger
                            .doOnNext { log.debug { lfmt(it, "trigger pre-throttle") } }
                            .throttleLast(1, TimeUnit.SECONDS)
                            .doOnNext { log.debug { lfmt(it, "starting dequeue") } }
                            .concatMap { _ ->
                                val sw = Stopwatch.createUnstarted()
                                var count: Int = 0

                                this.persistence.get(topic)
                                        // Counters
                                        .doOnSubscribe {
                                            count = 0; sw.reset(); sw.start()
                                        }
                                        .doOnNext { count++ }

                                        // Actual publishing
                                        .publishToWire()

                                        .doOnComplete {
                                            log.info { lfmt(topic, "dequeued ${count} in [${sw}]") }
                                        }

                                        // Subscribe on topic specific scheduler
                                        .subscribeOn(this.scheduler(topic))

                                        // Map processed batch back to trigger unit
                                        .ignoreElements()
                                        .toObservable<Unit>()
                            }
                            .onErrorReturn { e ->
                                val message = when (e) {
                                    is CompositeException -> e.exceptions.map { it.message }.joinToString(",")
                                    else -> e.message
                                }

                                log.error("Dequeue encountered error [${message}]")

                                // On dequeue errors, perform reconnect if applicable
                                this.reconnect()
                            }
                }
                .doFinally {
                    log.info("Dequeue terminated")
                }
                .subscribe()
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

            log.debug { "Stored message for [${topicName}] trigger [${this.isConnected}]" }

            if (this.isConnected) {
                // Trigger dequeue flow
                this.trigger(topicName)
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
     *
     * IMPORTANT: Disconnecting gracefully leads to (irrational) delayed
     * connection related errors on eg. publish with paho-1.2.0, which may
     * interrupt dequeuing eg. so `disconnect` should be always be called with `forcibly=true`
     * for reliable results.
     */
    override fun disconnect(forcibly: Boolean): Completable {
        this.lock.withLock {
            log.info("Disconnecting ${if (forcibly) "forcibly" else "gracefully"}")
            this.connectionSubscription = null
            return this.client.disconnect(forcibly)
        }
    }

    /**
     * Start remote broker connection.
     * The connection will be retried internally until it succeeds or {@link disconnect} is called.
     * The returned {@link Completable} will always be completed without error.
     */
    override fun connect(): Completable {
        this.lock.withLock {
            // Don't start another connection subscription if one is already active
            if (this.connectionSubscription == null) {
                log.info("Connection cycle initiating")
                // RxClient observables are hot, thus need to defer in order to re-subscribe properly on retry
                this.connectionSubscription = Completable
                        .defer {
                            log.info { "Establishing connection to ${this.client.uri}" }
                            this.client.connect()
                        }
                        .onErrorComplete {
                            when (it) {
                            // Avoid retries when already connected
                                is MqttException -> {
                                    val alreadyConnected = it.reasonCode == REASON_CODE_CLIENT_CONNECTED.toInt()

                                    if (alreadyConnected)
                                        log.info { "Client is already connected to ${this.client.uri}" }

                                    alreadyConnected
                                }
                                else -> false
                            }
                        }
                        .doOnComplete {
                            log.info("Connection cycle completed")
                        }
                        .retryWithExponentialBackoff(
                                initialDelay = Duration.ofSeconds(2),
                                maximumDelay = Duration.ofMinutes(2),
                                action = { retry, delay, error ->
                                    log.error("Connection failed [${error.message}] retry [${retry}] in ${delay}")
                                })
                        .subscribe()
            }
            return Completable.complete()
        }
    }

    /**
     * Reconnect (if connection subscription is active)
     */
    private fun reconnect() {
        this.lock.withLock {
            log.debug { "Reconnecting to ${this.client.uri}" }
            val hasConnectionSubscription = this.connectionSubscription != null

            if (this.connectionSubscription?.isDisposed ?: true)
                this.disconnect(forcibly = true)

            if (hasConnectionSubscription)
                this.connect()
        }
    }

    /**
     * Indicates connection status
     */
    val isConnected: Boolean
        get() = this.client.isConnected

    /**
     * Close down dispatcher
     */
    override fun close() {
        this.client.disconnect(true)
        // Dispose the dequeue subscription
        this.dequeueSubscription = null
    }
}