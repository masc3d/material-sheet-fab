package org.deku.leoz.central.config

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.sync.DatabaseSync
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Leoz-central database sync configuration
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
open class DatabaseSyncConfiguration {
    internal var log = LogFactory.getLog(DatabaseSyncConfiguration::class.java)

    @Inject
    private lateinit var databaseSync: DatabaseSync

    /** Scheduler  */
    private val executorService: ScheduledExecutorService

    init {
        this.executorService = Executors.newSingleThreadScheduledExecutor()
    }

    @PostConstruct
    fun onInitialize() {
        log.info("Starting database sync scheduler")

        this.executorService.scheduleWithFixedDelay(
                {
                    try {
                        databaseSync.sync()
                    } catch (e: Exception) {
                        log.error(e.getMessage(), e)
                    }
                },
                // Initial delay
                0,
                // Interval
                10, TimeUnit.MINUTES)
    }

    @PreDestroy
    fun onDestroy() {
        log.info("Shutting down database sync scheduler")
        this.executorService.shutdown()
        try {
            this.executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            log.error(e.getMessage(), e)
        }

    }
}
