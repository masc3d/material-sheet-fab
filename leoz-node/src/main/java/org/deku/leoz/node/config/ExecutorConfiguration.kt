package org.deku.leoz.node.config

import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.*
import java.util.concurrent.*
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

    companion object {
        const val CACHED = "CACHED"
    }

    /**
     * Scheduled thread executor
     */
    @Bean
    open fun scheduledThreadPool(): ScheduledExecutorService {
        return ScheduledThreadPoolExecutor(4);
    }

    @Bean
    @Qualifier(CACHED)
    open fun cachedThreadPool(): ExecutorService {
        return ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                SynchronousQueue<Runnable>())
    }

    private val pools = ArrayList<ExecutorService>()

    @PostConstruct
    fun onInitialize() {
        pools.add(scheduledThreadPool())
        pools.add(cachedThreadPool())
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