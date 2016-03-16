package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

/**
 * Thread executor configuration
 * Created by masc on 11/03/16.
 */
@Configuration
@Lazy(false)
open class ExecutorConfiguration {
    val log = LogFactory.getLog(ExecutorConfiguration::class.java.name)

    /**
     * Scheduled thread executor
     */
    @Bean
    open fun executorService(): ScheduledExecutorService {
        // TODO: ScheduledThreadPoolExecutor pool size is always fixed (so lame.). may require an additional caching thread pool alternatively for consumers of non-scheduled executor services
        val executor = ScheduledThreadPoolExecutor(16);
        executor.setKeepAliveTime(60, TimeUnit.SECONDS)
        executor.removeOnCancelPolicy = true
        return executor
    }
    private val executorService by lazy { executorService() }

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
        shutdown(listOf(this.executorService))
    }
}