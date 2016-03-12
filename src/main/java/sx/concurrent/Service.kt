package sx.concurrent

import org.apache.commons.logging.LogFactory
import sx.Disposable
import java.time.Duration
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Generic service base class
 * Created by masc on 12/03/16.
 * @param executorService Executor service to use
 */
abstract class Service(
        private val executorService: ScheduledExecutorService,
        interval: Duration? = null)
:
        ScheduledExecutorService,
        ExecutorService by executorService,
        Disposable {
    private val log = LogFactory.getLog(this.javaClass)
    private val lock = ReentrantLock()
    private val futures = ArrayList<ScheduledFuture<*>>()
    private var intervalInternal: Duration?

    private var hasBeenStarted = false
    private var isStarted = false

    /**
     * Indicates if dynamic scheduling is supported
     * (eg. scheduling additional futures during service runtime and restarting the service)
     */
    val isDynamicSchedulingSupported by lazy {
        this.executorService is ScheduledThreadPoolExecutor && this.executorService.removeOnCancelPolicy
    }

    /**
     *
     */
    val scheduledExecutor: ScheduledThreadPoolExecutor
        get() = this.executorService as ScheduledThreadPoolExecutor


    var scheduledFuture: ScheduledFuture<*>? = null

    init {
        this.intervalInternal = interval
    }

    private fun assertScheduledExecutor() {
        if (!isDynamicSchedulingSupported)
            throw IllegalStateException("Requires dynamic scheduling provided by ScheduledThreadPoolExecutor with removeOnCancelPolicy")
    }

    private fun assertIsStarted() {
        if (!isStarted)
            throw IllegalStateException("Service not started")
    }

    private fun scheduleFuture(b: () -> ScheduledFuture<*>): ScheduledFuture<*> {
        this.assertScheduledExecutor()

        this.lock.withLock {
            this.assertIsStarted()

            val future = b()
            this.futures.add(future)
            return future
        }
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return this.scheduleFuture { this.executorService.schedule(command, delay, unit) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V : Any?> schedule(command: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        return this.scheduleFuture { this.executorService.schedule(command, delay, unit) } as ScheduledFuture<V>
    }

    override fun scheduleAtFixedRate(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledFuture<*> {
        return this.scheduleFuture { this.executorService.scheduleAtFixedRate(command, initialDelay, period, unit) }
    }

    override fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        this.assertScheduledExecutor()
        return this.scheduleFuture { this.executorService.scheduleWithFixedDelay(command, initialDelay, delay, unit) }
    }

    /**
     * Service interval. Will imply a service restart when changed during service runtime (requires dynamic scheduling)
     */
    var interval: Duration?
        get() = this.intervalInternal
        set(value) {
            val wasStarted = this.isStarted
            this.stop()
            this.intervalInternal = value
            if (wasStarted)
                this.start()
        }

    /**
     * Starts the service, running the service logic at the @link interval specified
     */
    fun start() {
        this.lock.withLock {
            this.stop()

            if (this.hasBeenStarted && !this.isDynamicSchedulingSupported)
                throw IllegalStateException("This service has been stopped and cannot be restarted due to lack of dynamic scheduling")

            val interval = this.interval
            if (interval != null) {
                this.scheduledFuture = this.scheduleAtFixedRate({ this.runImpl() }, 0, interval.toMillis(), TimeUnit.MILLISECONDS)
            }
            this.isStarted = true
            this.hasBeenStarted = true
        }
    }

    /**
     * Stops the service
     */
    fun stop(interrupt: Boolean = false) {
        this.lock.withLock {
            /**
             * Cancel future function
             */
            val cancel = fun(f: ScheduledFuture<*>) {
                try {
                    f.cancel(interrupt)
                    f.get()
                } catch(e: Throwable) {
                    log.error(e.message, e)
                }
            }

            val scheduledFuture = this.scheduledFuture

            // Collect futures
            val futures = ArrayList<ScheduledFuture<*>>()
            if (scheduledFuture != null) {
                futures.add(scheduledFuture)
            }
            futures.addAll(this.futures)

            // Cancel futures
            futures.forEach {
                cancel(it)
            }

            this.futures.clear()
            this.scheduledFuture = null
            this.isStarted = false
        }
    }

    /**
     * Triggers the service logic to run once
     */
    fun trigger() {
        this.lock.withLock {
            this.assertIsStarted()
            this.executorService.submit({ this.runImpl() })
        }
    }

    @Synchronized private fun runImpl() {
        this.run()
    }

    /**
     * Service logic implementation
     */
    abstract protected fun run()

    override fun close() {
        this.stop()
    }

    protected fun finalize() {
        this.close()
    }
}