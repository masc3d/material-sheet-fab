package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
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
    open fun scheduledThreadPool(): ScheduledExecutorService {
        val executor = ScheduledThreadPoolExecutor(4);
        executor.setKeepAliveTime(60, TimeUnit.SECONDS)
        return executor
    }

    private val pools = ArrayList<ExecutorService>()

    @PostConstruct
    fun onInitialize() {
        pools.add(scheduledThreadPool())
    }

    @PreDestroy
    fun onDestroy() {
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

        shutdown(this.pools)
    }
}