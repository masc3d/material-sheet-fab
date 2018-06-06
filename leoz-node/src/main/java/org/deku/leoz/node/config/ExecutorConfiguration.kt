package org.deku.leoz.node.config

import io.reactivex.schedulers.Schedulers
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

    /**
     * Shutdown executors gracefully
     */
    private fun List<ExecutorService>.shutdownGracefully() {
        this.forEach {
            log.info("Shutting down [${it}]")
            it.shutdown()
        }

        this.forEach {
            try {
                it.awaitTermination(15, TimeUnit.SECONDS)
                log.info("Graceful shutdown of [${it}]")
            } catch (e: InterruptedException) {
                log.warn("Shutdown of [${it}] timed out: ${e.message}")
            }
        }
    }

    @PreDestroy
    fun onDestroy() {
        log.info("Shutting down rx schedulers")
        // Explicitly shutdown rx schedulers
        Schedulers.shutdown()

        // Shutdown executors individually, as composite executor doesn't support awaiting for termination yet
        listOf(
                this.scheduledExecutorService,
                this.cachedExecutorService
        ).shutdownGracefully()
    }
}