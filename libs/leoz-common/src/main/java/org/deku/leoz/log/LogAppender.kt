package org.deku.leoz.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import sx.Disposable
import sx.Dispose
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.converters.DefaultConverter
import sx.jms.embedded.Broker
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Log appender sending log messages via jms
 * Created by masc on 11.06.15.
 */
public class LogAppender(
        /** Messaging context */
        private val messagingConfiguration: MessagingConfiguration,
        private val idenity: Identity)
:
        AppenderBase<ILoggingEvent>(),
        Disposable {
    private val log = LogFactory.getLog(this.javaClass)
    /** Message converter  */
    private val converter: Converter
    /** Log message buffer  */
    private val buffer = ArrayList<LogMessage.LogEntry>()
    /** Flush scheduler  */
    private var executorService: ScheduledExecutorService? = null

    /**
     * Broker listener, jms destination is automatically created when broker start is detected
     */
    var brokerEventListener: Broker.EventListener = object : Broker.DefaultEventListener() {
        override fun onStart() {
            executorService!!.scheduleAtFixedRate(
                    { flush() },
                    0,
                    5,
                    TimeUnit.SECONDS)
        }

        override fun onStop() {
            Dispose.safely(this@LogAppender)
        }
    }

    init {
        converter = DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP)
    }

    /**
     * Flush log messages to underlying jms broker
     */
    private fun flush() {
        var logMessageBuffer = ArrayList<LogMessage.LogEntry>()

        synchronized (this.buffer) {
            logMessageBuffer.addAll(this.buffer)
            this.buffer.clear()
        }

        if (logMessageBuffer.size > 0) {
            log.trace("Flushing [${logMessageBuffer.size}]")
            try {
                Channel(messagingConfiguration.centralLogQueue).use {
                    it.send(LogMessage(
                            this.idenity.key,
                            logMessageBuffer.toTypedArray()))
                }
            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    override fun append(eventObject: ILoggingEvent) {
        val le = eventObject as LoggingEvent
        synchronized (buffer) {
            buffer.add(LogMessage.LogEntry(le))
        }
    }

    @Synchronized override fun start() {
        if (executorService != null) {
            this.stop()
        }

        executorService = Executors.newScheduledThreadPool(1)

        messagingConfiguration.broker.delegate.add(brokerEventListener)
        if (messagingConfiguration.broker.isStarted)
            brokerEventListener.onStart()

        super.start()
    }

    @Synchronized override fun stop() {
        if (executorService != null) {
            // Immediate flush and subsequent shutdown
            executorService!!.shutdownNow()

            // Wait for termination
            try {
                executorService!!.awaitTermination(java.lang.Long.MAX_VALUE, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                log.error(e.message, e)
            }

            executorService = null
        }

        // Final flush
        try {
            if (messagingConfiguration.broker.isStarted)
                this.flush()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

        messagingConfiguration.broker.delegate.remove(brokerEventListener)

        super.stop()
    }

    override fun close() {
        try {
            this.stop()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
