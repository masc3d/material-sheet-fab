package sx.concurrent

import org.apache.commons.logging.LogFactory
import sx.Disposable
import java.time.Duration
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Generic service base class.
 * Created by masc on 12/03/16.
 * @param executorService Executor service to use
 * @param initialDelay Initial delay. If not provided and period is not set either, the task will not initially execute (trigger only)
 * @param period If provided the service task will be scheduled at a fixed rate.
 */
abstract class Service(
        open protected val executorService: ScheduledExecutorService,
        open val initialDelay: Duration? = null,
        period: Duration? = null)
:
        Disposable {
    private val log = LogFactory.getLog(this.javaClass)
    private val lock = ReentrantLock()

    /** Primary service task */
    private var serviceTask: TaskFuture? = null
    private val supplementalTasks = ArrayList<TaskFuture>()

    private var periodInternal: Duration?

    private var hasBeenStarted = false
    private var isStarted = false

    /**
     * Runnable task interface
     */
    interface RunnableTask : Runnable {
        fun waitForCompletion()
    }

    /**
     * Service task runnable  with completion event support
     */
    inner class Task(val command: () -> Unit) : RunnableTask {
        val evt = ManualResetEvent(true)

        override fun run() {
            evt.reset()
            try {
                command()
            } catch(e: Throwable) {
                log.info(e.message, e)
            } finally {
                evt.set()
            }
        }

        /**
         * Wait for completion of runnable command
         */
        override fun waitForCompletion() {
            this.evt.waitOne()
        }
    }

    /**
     * Service task, tying together and decorating Future and RunnableTask
     */
    @Suppress("UNCHECKED_CAST")
    class TaskFuture(private val future: Future<*>,
                          private val runnableTask: RunnableTask)
    :
            Future<Any?> by future as Future<Any?>,
            RunnableTask by runnableTask {

        /**
         * Cancel task
         * @param interrupt Allow interruption
         * @param wait Wait for task to finish
         */
        fun cancel(interrupt: Boolean, wait: Boolean) {
            this.cancel(interrupt)
            if (wait) {
                this.waitForCompletion()
            }
        }
    }

    /**
     * Schedule a task
     * @param command Task code block
     * @param initialDelay Initial delay for schedule
     * @param period Period for schedule at a fixed rate
     */
    private fun ScheduledExecutorService.scheduleTask(command: () -> Unit, initialDelay: Duration = Duration.ZERO, period: Duration? = null): TaskFuture {
        val task = Task(command)

        val future: Future<*> =
                if (period != null) {
                    this.scheduleAtFixedRate(task, initialDelay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS)
                } else {
                    this.schedule(task, initialDelay.toMillis(), TimeUnit.MILLISECONDS)
                }

        return TaskFuture(
                future = future,
                runnableTask = task)
    }

    /**
     * Submit a task
     * @param command Task code bflock
     */
    private fun ExecutorService.submitTask(command: () -> Unit): TaskFuture {
        val task = Task(command)
        return TaskFuture(
                future = this.submit(task),
                runnableTask = task)
    }

    /**
     * Indicates if dynamic scheduling is supported
     * (eg. scheduling additional futures during service runtime and restarting the service)
     */
    val isDynamicSchedulingSupported by lazy {
        val exc = this.executorService
        exc is ScheduledThreadPoolExecutor && exc.removeOnCancelPolicy
    }

    init {
        this.periodInternal = period
    }

    private fun assertScheduledExecutor() {
        if (!isDynamicSchedulingSupported)
            throw IllegalStateException("Requires dynamic scheduling provided by ScheduledThreadPoolExecutor with removeOnCancelPolicy")
    }

    private fun assertIsStarted() {
        if (!isStarted)
            throw IllegalStateException("Service not started")
    }

    /**
     * Service interval. Will imply a service restart when changed during service runtime (requires dynamic scheduling)
     */
    var period: Duration?
        get() = this.periodInternal
        set(value) {
            log.info("Changing interval to [${value}]")
            val wasStarted = this.isStarted
            this.stop(log = false)
            this.periodInternal = value
            if (wasStarted)
                this.start(log = false)
        }

    /**
     * Submit a supplemental task, which is tracked and also stopped together with the service
     * @param command Supplemental task code block
     */
    protected fun submitSupplementalTask(command: () -> Unit) {
        this.assertIsStarted()

        this.lock.withLock {
            this.supplementalTasks.add(
                    this.executorService.submitTask(command))
        }
    }

    /**
     * Restart service
     */
    fun restart() {
        this.stop()
        this.start()
    }

    /**
     * Starts the service, running the service logic at the @link interval specified
     */
    fun start(log: Boolean = true) {
        this.lock.withLock {
            if (this.isStarted) {
                this.log.info("Service [${this.javaClass}] has already been started")
                return
            }

            if (log)
                this.log.info("Starting service [${this.javaClass}]")

            if (this.hasBeenStarted && !this.isDynamicSchedulingSupported)
                throw IllegalStateException("This service has been stopped and cannot be restarted due to lack of dynamic scheduling")

            // Only schedule initially if either period or initial delay is set (or both)
            if (this.period != null || this.initialDelay != null) {
                this.serviceTask = this.executorService.scheduleTask(
                        command = { this.runImpl() },
                        initialDelay = this.initialDelay ?: Duration.ZERO,
                        period = this.period)
            }
            this.isStarted = true
            this.hasBeenStarted = true
        }
    }

    /**
     * Stops the service
     * @param interrupt Interrupt futures
     * @param async Perform stopping asynchronously (required when called from within service thread)
     */
    fun stop(interrupt: Boolean = true, async: Boolean = false, log: Boolean = true) {
        val stopRunnable = Runnable {
            val wasStarted = this.isStarted
            if (log && wasStarted)
                this.log.info("Stopping service [${this.javaClass}]")

            this.lock.withLock {
                val tasks = ArrayList<TaskFuture>()

                this.serviceTask?.let {
                    tasks.add(it)
                }
                tasks.addAll(this.supplementalTasks)

                tasks.forEach {
                    it.cancel(interrupt, wait = true)
                }

                this.supplementalTasks.clear()
                this.serviceTask = null
                this.isStarted = false
            }
            if (log && wasStarted)
                this.log.info("Stopped service [${this.javaClass}]")
        }

        if (async) {
            this.executorService.submit(stopRunnable)
        } else {
            stopRunnable.run()
        }
    }

    /**
     * Triggers the service logic to run once
     */
    fun trigger() {
        this.submitSupplementalTask({ this.runImpl() })
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