package sx.concurrent.task

import java.util.concurrent.*

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
        ExecutorService by executorService {
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
        throw UnsupportedOperationException()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        throw UnsupportedOperationException()
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