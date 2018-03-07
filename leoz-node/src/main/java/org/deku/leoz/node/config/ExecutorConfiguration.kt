package org.deku.leoz.node.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.concurrent.task.CompositeExecutorService
import java.util.concurrent.*
import javax.annotation.PreDestroy

/**
 * Thread executor configuration
 * Created by masc on 11/03/16.
 */
@Configuration
@Lazy(false)
class ExecutorConfiguration {
    val log = LoggerFactory.getLogger(ExecutorConfiguration::class.java.name)

    /**
     * Scheduled thread executor
     */
    @get:Bean
    val executorService: ScheduledExecutorService
        get() = CompositeExecutorService(
                this.scheduledExecutorService,
                this.cachedExecutorService)

    /**
     * Scheduled executor service
     */
    private val scheduledExecutorService by lazy {
        val executor = ScheduledThreadPoolExecutor(8)
        executor.removeOnCancelPolicy = true
        executor
    }

    /**
     * Cached pool executor service
     */
    private val cachedExecutorService by lazy {
        ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                SynchronousQueue<Runnable>())
    }

    @PreDestroy
    fun onDestroy() {
        /**
         * Shutdown helper
         */
        val shutdown = fun(exc: List<ExecutorService>) {
            exc.forEach {
                log.info("Shutting down [${it}]")
                it.shutdown()
            }

            exc.forEach {
                try {
                    it.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
                } catch (e: InterruptedException) {
                    log.error(e.message, e)
                }
            }
        }

        // Shutdown executors
        shutdown(listOf(this.scheduledExecutorService, this.cachedExecutorService))
    }
}