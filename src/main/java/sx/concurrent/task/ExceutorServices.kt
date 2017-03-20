package sx.concurrent.task

import org.slf4j.LoggerFactory
import java.util.concurrent.*

/**
 * Interface for a dynamic scheduled executor service, which eg. supports dynamically removing scheduled tasks on cancel.
 */
interface DynamicScheduledExecutorService {
    var removeOnCancelPolicy: Boolean
}

/**
 * Delegating executor service, which will track and maintain tasks submitted via this instance.
 * Specific behaviour applies to shutdown which will not shutdown the executor service itself,
 * but only tasks that have been submitted through this instance.
 * TODO: complete implementation, tracking
 * Created by masc on 16/03/16.
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class TaskExecutorService(
        protected val executorService: ExecutorService)
:
        AbstractExecutorService(),
        ExecutorService by executorService {

    override fun <T : Any?> newTaskFor(p0: Runnable?, p1: T): RunnableFuture<T>? {
        return super.newTaskFor(p0, p1)
    }

    override fun <T : Any?> newTaskFor(p0: Callable<T>?): RunnableFuture<T>? {
        return super.newTaskFor(p0)
    }
}

/**
 * Delegating scheduled executor service, tracking scheduled tasks
 * See base class {@link TaskExecutorService} for details.
 * TODO: complete implementation/tracking
 * Created by masc on 16/03/16.
 * @param scheduledExecutorService A scheduled executor service to use (for scheduling only, except executorService is omitted)
 * @param executorService Regular executor service for regular submit
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class ScheduledTaskExecutorService(
        protected val compositeExecutorService: CompositeExecutorService,
        executorService: ExecutorService = compositeExecutorService)
:
        TaskExecutorService(executorService),
        ScheduledExecutorService by compositeExecutorService,
        DynamicScheduledExecutorService by compositeExecutorService {
}

/**
 * Composite executor service, combining a scheduled executor service and an alternative executor service for
 * regularly submitting tasks.
 * This class does not take Ownership of provided executor services therefore any
 * management tasks (eg. shutting down) are unsupported and will throw.
 * @param scheduledExecutorService Executor service for scheduling
 * @param executorService Executor service for submitting. Will fall back to scheduledExecutorService if not provided.
 * Created by masc on 18/03/16.
 */
class CompositeExecutorService(
        private val scheduledExecutorService: ScheduledExecutorService,
        private val executorService: ExecutorService = scheduledExecutorService)
:
        ScheduledExecutorService by scheduledExecutorService,
        ExecutorService by executorService,
        DynamicScheduledExecutorService {

    companion object {
        private val log by lazy { LoggerFactory.getLogger(CompositeExecutorService::class.java) }

        //region Convenience methods for creating instances

        /**
         * Create instance
         */
        @JvmStatic fun create(scheduledCorePoolSize: Int = 0,
                              cachedCorePoolSize: Int = 0,
                              cachedKeepAliveTimeSeconds: Int = 60): CompositeExecutorService {
            val scheduledExecutorService = ScheduledThreadPoolExecutor(scheduledCorePoolSize);

            try {
                scheduledExecutorService.removeOnCancelPolicy = true
            } catch(e: IllegalAccessError) {
                log.warn("Remove on cancel policy not supported")
            }

            val cachedExecutorService = ThreadPoolExecutor(
                    cachedCorePoolSize,
                    Integer.MAX_VALUE,
                    cachedKeepAliveTimeSeconds.toLong(),
                    TimeUnit.SECONDS,
                    SynchronousQueue<Runnable>())

            return CompositeExecutorService(scheduledExecutorService, cachedExecutorService)
        }
        // endregion
    }

    override var removeOnCancelPolicy: Boolean
        get() {
            return when (this.scheduledExecutorService) {
                is ScheduledThreadPoolExecutor -> scheduledExecutorService.removeOnCancelPolicy
                else -> false
            }
        }
        set(value) {
            when (this.scheduledExecutorService) {
                is ScheduledThreadPoolExecutor -> scheduledExecutorService.removeOnCancelPolicy = value
                else -> throw UnsupportedOperationException()
            }
        }

    override fun submit(task: Runnable): Future<*> {
        return this.executorService.submit(task)
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        return this.executorService.submit(task)
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return this.executorService.submit(task, result)
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        return this.executorService.invokeAny(tasks)
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        return this.executorService.invokeAny(tasks, timeout, unit)
    }

    override fun isTerminated(): Boolean {
        return this.executorService.isTerminated && this.scheduledExecutorService.isTerminated
    }

    override fun shutdown() {
        this.executorService.shutdown()
        this.scheduledExecutorService.shutdown()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        val list1 =  this.executorService.shutdownNow()
        val list2 =  this.scheduledExecutorService.shutdownNow()
        list1.addAll(list2)
        return list1
    }

    override fun isShutdown(): Boolean {
        return this.executorService.isShutdown && this.scheduledExecutorService.isShutdown
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        return this.executorService.invokeAll(tasks)
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): MutableList<Future<T>> {
        return this.executorService.invokeAll(tasks, timeout, unit)
    }

    override fun execute(command: Runnable) {
        this.executorService.execute(command)
    }
}