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
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Log appender sending log messages via mq
 *
 * IMPORTANT NOTE: the dispatcher service of this appender must be controlled manually as it depends on
 * a healthy client connection. This implementation is not suited for long time operation without
 * having a dispatcher started as all entries are cached in-memory.
 * Either a connection should be available constantly (local broker) or the provided mq client must be capable
 * of offline buffering.
 *
 * TODO: add support for reactive dispatching, depending on broker/client connection
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
    inner class Dispatcher(executorService: ScheduledExecutorService)
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

            synchronized(this@LogMqAppender.buffer) {
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

    private val executor: ScheduledExecutorService by lazy {
        val executor = Executors.newScheduledThreadPool(1) as ScheduledThreadPoolExecutor
        try {
            executor.removeOnCancelPolicy = true
        } catch(e: Throwable) {
            log.warn("Could not set ScheduledExecutorService.removeOnCancelPolicy [${e.message}]")
        }
        executor
    }

    val dispatcher: Dispatcher = Dispatcher(this.executor)

    override fun append(eventObject: ILoggingEvent) {
        val le = eventObject as LoggingEvent
        synchronized(buffer) {
            buffer.add(LogMessage.LogEntry(le))
        }
    }

    fun flush() {
        this.dispatcher.trigger()
    }

    override fun start() {
        this.lock.withLock {
            super.start()
        }
    }

    override fun stop() {
        this.lock.withLock {
            if (this.isStarted)
                this.dispatcher.trigger()
            // Shutdown log flush gracefully
            this.dispatcher.stop()
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
