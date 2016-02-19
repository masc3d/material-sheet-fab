package org.deku.leoz.central.config

import org.apache.commons.logging.LogFactory
import org.deku.leoz.central.data.sync.DatabaseSync
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * Leoz-central database sync configuration
 * Created by masc on 30.06.15.
 */
@Configuration
@Lazy(false)
open class DatabaseSyncConfiguration {
    private var log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var databaseSync: DatabaseSync

    /** Scheduler  */
    private val executorService: ScheduledExecutorService

    /** Indicates if scheduler has been started */
    private var isStarted = false

    init {
        this.executorService = Executors.newSingleThreadScheduledExecutor()
    }

    /** Broker event listener  */
    private val brokerEventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            this@DatabaseSyncConfiguration.start()
        }

        override fun onStop() {
            this@DatabaseSyncConfiguration.stop()
        }
    }

    fun trigger() {
        log.info("Triggering database sync")
        this.executorService.submit {
            try {
                databaseSync.sync()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    /**
     * Start database sync scheduler
     */
    @Synchronized private fun start() {
        if (this.isStarted)
            return

        log.info("Starting database sync scheduler")
        this.executorService.scheduleWithFixedDelay(
                {
                    try {
                        databaseSync.sync()
                    } catch (e: Exception) {
                        log.error(e.message, e)
                    }
                },
                // Initial delay
                0,
                // Interval
                10, TimeUnit.MINUTES)

        this.isStarted = true
    }

    /**
     * Stop database sync scheduler
     */
    @Synchronized private fun stop() {
        log.info("Shutting down database sync scheduler")
        this.executorService.shutdown()
        try {
            this.executorService.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            log.error(e.message, e)
        }
    }

    /**
     * On initialization
     */
    @PostConstruct
    fun onInitialize() {
        // Wire broker event
        ActiveMQBroker.instance.delegate.add(brokerEventListener)

        if (ActiveMQBroker.instance.isStarted)
            brokerEventListener.onStart()
    }
}
