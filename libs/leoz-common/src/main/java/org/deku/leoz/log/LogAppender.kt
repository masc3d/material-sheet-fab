package org.deku.leoz.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.Lifecycle
import sx.jms.Channel
import sx.jms.Broker
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Log appender sending log messages via jms
 * Created by masc on 11.06.15.
 */
class LogAppender(
        /** Messaging context */
        private val broker: Broker,
        private val logChannelConfiguration: Channel.Configuration,
        private val identitySupplier: () -> Identity)
:
        AppenderBase<ILoggingEvent>(),
        Lifecycle,
        Disposable {

    private val log = LoggerFactory.getLogger(this.javaClass)
    /** Log message buffer  */
    private val buffer = ArrayList<LogMessage.LogEntry>()

    /**
     * Flush Service
     */
    inner private class Service(executorService: ScheduledExecutorService)
    :
            sx.concurrent.Service(
                    executorService = executorService,
                    period = Duration.ofSeconds(5),
                    interruptOnCancel = false) {
        /**
         * Flush service implementation
         */
        override fun run() {
            // Flush log messages to underlying jms broker
            val logMessageBuffer = ArrayList<LogMessage.LogEntry>()

            synchronized (this@LogAppender.buffer) {
                logMessageBuffer.addAll(this@LogAppender.buffer)
                this@LogAppender.buffer.clear()
            }

            if (logMessageBuffer.size > 0) {
                log.trace("Flushing [${logMessageBuffer.size}]")
                try {
                    Channel(this@LogAppender.logChannelConfiguration).use {
                        it.send(LogMessage(
                                this@LogAppender.identitySupplier().key,
                                logMessageBuffer.toTypedArray()))
                    }
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            }
        }
    }

    private val service: Service = Service(Executors.newSingleThreadScheduledExecutor())

    /**
     * Broker listener, jms destination is automatically created when broker start is detected
     */
    val brokerEventListener: Broker.EventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            if (this@LogAppender.isStarted)
                service.start()
        }

        override fun onStop() {
            this@LogAppender.stop()
            if (service.isStarted)
                service.trigger()
            service.stop()
        }
    }

    init {
        this.broker.delegate.add(brokerEventListener)
    }

    override fun append(eventObject: ILoggingEvent) {
        val le = eventObject as LoggingEvent
        synchronized (buffer) {
            buffer.add(LogMessage.LogEntry(le))
        }
    }

    @Synchronized override fun start() {
        super.start()
        if (this.broker.isStarted)
            this.service.start()
    }

    @Synchronized override fun stop() {
        // Shutdown log flush gracefully
        this.service.stop()
        super.stop()
    }

    override fun restart() {
        this.stop()
        this.start()
    }

    override fun isRunning(): Boolean {
        return this.isStarted
    }

    override fun close() {
        this.stop()
    }
}
