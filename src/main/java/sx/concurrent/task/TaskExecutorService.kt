package sx.concurrent.task

import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.RunnableFuture

/**
 * Delegating executor service, which will track and maintain tasks submitted via this instance.
 * Specific behaviour applies to shutdown which will not shutdown the executor service itself,
 * but only tasks that have been submitted through this instance.
 * Created by masc on 16/03/16.
 */
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