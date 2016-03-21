package sx.concurrent

import org.apache.commons.logging.LogFactory
import sx.Disposable
import sx.concurrent.task.DynamicScheduledExecutorService
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
        protected val executorService: ScheduledExecutorService,
        val initialDelay: Duration? = null,
        period: Duration? = null)
:
        Disposable {
    private val log = LogFactory.getLog(this.javaClass)
    private val lock = ReentrantLock()

    /** Primary service task future */
    private var serviceTaskFuture: TaskFuture? = null
    /** Supplemental task futures */
    private val supplementalTaskFutures = HashMap<Task, TaskFuture>()

    /** Internal period holder */
    private var periodInternal: Duration?

    private var hasBeenStarted = false
    private var isStarted = false
    /** Indicates if the service is currently triggered, to prevent multiple triggers stacking up */
    @Volatile private var isTriggered: Boolean = false

    /**
     * Runnable task interface
     */
    interface RunnableTask : Runnable {
        fun waitForCompletion()
    }

    /**
     * Service task runnable  with completion event support
     */
    inner class Task(
            private val command: () -> Unit,
            private val onCompletion: ((task: Task) -> Unit)? = null) : RunnableTask {
        private val evt = ManualResetEvent(true)

        override fun run() {
            evt.reset()
            try {
                command()
            } catch(e: Throwable) {
                log.error(e.message, e)
            } finally {
                val onCompletion = this.onCompletion
                if (onCompletion != null) {
                    try {
                        onCompletion(this)
                    } catch(e: Throwable) {
                        log.error(e.message, e)
                    }
                }

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
    class TaskFuture(
            private val future: Future<*>,
            val task: Task)
    :
            Future<Any?> by future as Future<Any?>,
            RunnableTask by task {

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
    private fun ScheduledExecutorService.scheduleTask(
            task: Task,
            initialDelay: Duration = Duration.ZERO,
            period: Duration? = null): TaskFuture {

        val future: Future<*> =
                if (period != null) {
                    this.scheduleAtFixedRate(task, initialDelay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS)
                } else {
                    this.schedule(task, initialDelay.toMillis(), TimeUnit.MILLISECONDS)
                }

        return TaskFuture(
                future = future,
                task = task)
    }

    /**
     * Submit a task
     * @param command Task code bflock
     */
    private fun ExecutorService.submitTask(task: Task): TaskFuture {
        return TaskFuture(
                future = this.submit(task),
                task = task)
    }

    /**
     * Indicates if dynamic scheduling is supported
     * (eg. scheduling additional futures during service runtime and restarting the service)
     */
    val isDynamicSchedulingSupported by lazy {
        val exc = this.executorService
        when (exc) {
            is ScheduledThreadPoolExecutor -> exc.removeOnCancelPolicy
            is DynamicScheduledExecutorService -> exc.removeOnCancelPolicy
            else -> false
        }

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
        this.lock.withLock {
            this.assertIsStarted()
            val task = Task(
                    command = command,
                    // Completion handler which will remove this task from the internal supplemental future map
                    onCompletion = {
                        synchronized(this.supplementalTaskFutures) {
                            this.supplementalTaskFutures.remove(it)
                        }
                    })
            // Add to supplemental future map
            this.supplementalTaskFutures.put(task, this.executorService.submitTask(task))
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

            this.onStart()

            // Only schedule initially if either period or initial delay is set (or both)
            if (this.period != null || this.initialDelay != null) {
                this.serviceTaskFuture = this.executorService.scheduleTask(
                        task = Task({ this.runImpl() }),
                        initialDelay = this.initialDelay ?: Duration.ZERO,
                        period = this.period)
            }
            this.isStarted = true
            this.hasBeenStarted = true
        }
    }

    /**
     * On start handler, optional override
     */
    open fun onStart() {
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
                this.log.info("Stopping service [${this.javaClass}] interrupt [${interrupt}]")

            this.lock.withLock {
                val tasks = ArrayList<TaskFuture>()

                this.serviceTaskFuture?.let {
                    tasks.add(it)
                }
                synchronized(this.supplementalTaskFutures) {
                    tasks.addAll(this.supplementalTaskFutures.values)
                }

                tasks.forEach {
                    it.cancel(interrupt, wait = true)
                }

                this.supplementalTaskFutures.clear()
                this.serviceTaskFuture = null
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
     * Triggers the service logic to run once.
     * Puts the service in 'triggered' mode. All further triggers until the next schedule (which will execute asap) will be ignored.
     */
    fun trigger() {
        this.lock.withLock {
            this.assertIsStarted()

            if (this.isTriggered)
                return

            // runImpl being synchronized will ensure triggered task will execute in order
            this.submitSupplementalTask({ this.runImpl() })

            this.isTriggered = true
        }
    }

    @Synchronized private fun runImpl() {
        try {
            this.run()
        } finally {
            this.isTriggered = false
        }
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