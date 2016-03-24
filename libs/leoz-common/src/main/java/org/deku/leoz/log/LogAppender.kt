package org.deku.leoz.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import sx.Disposable
import sx.jms.Channel
import sx.jms.embedded.Broker
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Log appender sending log messages via jms
 * Created by masc on 11.06.15.
 */
public class LogAppender(
        /** Messaging context */
        private val messagingConfiguration: MessagingConfiguration,
        private val idenitySupplier: () -> Identity)
:
        AppenderBase<ILoggingEvent>(),
        Disposable {
    private val log = LogFactory.getLog(this.javaClass)
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
            var logMessageBuffer = ArrayList<LogMessage.LogEntry>()

            synchronized (this@LogAppender.buffer) {
                logMessageBuffer.addAll(this@LogAppender.buffer)
                this@LogAppender.buffer.clear()
            }

            if (logMessageBuffer.size > 0) {
                log.trace("Flushing [${logMessageBuffer.size}]")
                try {
                    Channel(messagingConfiguration.centralLogQueue).use {
                        it.send(LogMessage(
                                this@LogAppender.idenitySupplier().key,
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
            service.trigger()
            service.stop()
        }
    }

    init {
        messagingConfiguration.broker.delegate.add(brokerEventListener)
    }


    override fun append(eventObject: ILoggingEvent) {
        val le = eventObject as LoggingEvent
        synchronized (buffer) {
            buffer.add(LogMessage.LogEntry(le))
        }
    }

    @Synchronized override fun start() {
        super.start()
        if (messagingConfiguration.broker.isStarted)
            brokerEventListener.onStart()
    }

    @Synchronized override fun stop() {
        // Shutdown log flush gracefully
        this.service.stop()
        super.stop()
    }

    override fun close() {
        this.stop()
    }
}
