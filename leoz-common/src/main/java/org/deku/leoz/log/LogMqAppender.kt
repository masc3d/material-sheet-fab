package org.deku.leoz.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase
import org.deku.leoz.identity.Identity
import org.slf4j.LoggerFactory
import sx.Disposable
import sx.Lifecycle
import sx.mq.MqClient
import sx.time.Duration
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Log appender sending log messages via mq
 * Created by masc on 11.06.15.
 */
class LogMqAppender(
        private val clientSupplier: () -> MqClient,
        private val identitySupplier: () -> Identity)
:
        AppenderBase<ILoggingEvent>(),
        Lifecycle,
        Disposable {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val lock = ReentrantLock()
    /** Log message buffer */
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

            synchronized (this@LogMqAppender.buffer) {
                logMessageBuffer.addAll(this@LogMqAppender.buffer)
                this@LogMqAppender.buffer.clear()
            }

            if (logMessageBuffer.size > 0) {
                log.trace("Flushing [${logMessageBuffer.size}]")
                try {
                    val identity = this@LogMqAppender.identitySupplier()
                    this@LogMqAppender.clientSupplier().use {
                        it.send(LogMessage(
                                nodeType = identity.name,
                                nodeKey = identity.key.value,
                                logEntries = logMessageBuffer.toTypedArray()))
                    }
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            }
        }
    }

    private val service: Service = Service(Executors.newSingleThreadScheduledExecutor())

    override fun append(eventObject: ILoggingEvent) {
        val le = eventObject as LoggingEvent
        synchronized (buffer) {
            buffer.add(LogMessage.LogEntry(le))
        }
    }

    fun flush() {
        this.service.trigger()
    }

    override fun start() {
        this.lock.withLock {
            super.start()
            this.service.start()
        }
    }

    override fun stop() {
        this.lock.withLock {
            if (this.isStarted)
                this.service.trigger()
            // Shutdown log flush gracefully
            this.service.stop()
            super.stop()
        }
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
