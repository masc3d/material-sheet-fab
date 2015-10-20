package org.deku.leoz.log

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.config.MessagingConfiguration
import sx.Disposable
import sx.Dispose
import sx.jms.Converter
import sx.jms.converters.DefaultConverter
import sx.jms.embedded.Broker

import javax.jms.Connection
import javax.jms.DeliveryMode
import javax.jms.MessageProducer
import javax.jms.Session
import java.util.ArrayList
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Log appender sending log messages via jms
 * Created by masc on 11.06.15.
 */
public class LogAppender(
        /** Messaging context */
        private val messagingConfiguration: MessagingConfiguration) : AppenderBase<ILoggingEvent>(), Disposable {

    private val log = LogFactory.getLog(this.javaClass)
    /** Message converter  */
    private val converter: Converter
    /** Log message buffer  */
    private val buffer = ArrayList<LogMessage>()
    /** Flush scheduler  */
    private var executorService: ScheduledExecutorService? = null

    /**
     * Broker listener, jms destination is automatically created when broker start is detected
     */
    var brokerEventListener: Broker.EventListener = object : Broker.EventListener {
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
        var logMessageBuffer = ArrayList<LogMessage>()

        synchronized (this.buffer) {
            logMessageBuffer.addAll(this.buffer)
            this.buffer.clear()
        }

        if (logMessageBuffer.size() > 0) {
            log.trace("Flushing [${logMessageBuffer.size()}]")
            try {
                val cn = messagingConfiguration.broker.connectionFactory.createConnection()
                cn.start()
                val session = cn.createSession(true, Session.AUTO_ACKNOWLEDGE)

                val mp = session.createProducer(messagingConfiguration.centralLogQueue)
                mp.deliveryMode = DeliveryMode.PERSISTENT
                // Log messages live a few days before they are purged by the broker
                mp.timeToLive = TimeUnit.DAYS.toMillis(2)
                mp.priority = 1
                mp.send(converter.toMessage(
                        logMessageBuffer.toArray<LogMessage>(arrayOfNulls<LogMessage>(0)),
                        session))

                session.commit()
            } catch (e: Exception) {
                log.error(e.getMessage(), e)
            }

        }
    }

    override fun append(eventObject: ILoggingEvent) {
        val le = eventObject as LoggingEvent
        synchronized (buffer) {
            buffer.add(LogMessage(le))
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
                log.error(e.getMessage(), e)
            }

            executorService = null
        }

        // Final flush
        try {
            if (messagingConfiguration.broker.isStarted)
                this.flush()
        } catch (e: Exception) {
            log.error(e.getMessage(), e)
        }

        messagingConfiguration.broker.delegate.remove(brokerEventListener)

        super.stop()
    }

    override fun dispose() {
        try {
            this.stop()
        } catch (e: Exception) {
            log.error(e.getMessage(), e)
        }

    }
}
