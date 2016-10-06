package org.deku.leoz.log

import ch.qos.logback.classic.Logger
import org.deku.leoz.Identity
import org.deku.leoz.MessagingTest
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.ArtemisConfiguration
import org.junit.Ignore
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener
import sx.logging.slf4j.*

import javax.jms.JMSException
import java.util.concurrent.Executors

/**
 * @author masc
 */
@Ignore
class LogTest : MessagingTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    @Throws(JMSException::class)
    fun testSend() {
        // Setup log appender
        val logAppender = LogAppender(
                broker = this.broker,
                logChannelConfiguration= ArtemisConfiguration.centralLogQueue,
                identitySupplier = { Identity.create(BundleType.LEOZ_NODE.value, SystemInformation.create()) })
        logAppender.start()

        val logRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        logRoot.addAppender(logAppender)

        // Generate some log messages
        for (i in 0..99)
            logRoot.info("Test!")

        // Dispose to make sure everything is flushed
        logAppender.flush()
        logAppender.close()
    }

    @Test
    @Throws(JMSException::class, InterruptedException::class)
    fun testReceive() {
        // Setup log message listener
        val listener = object : SpringJmsListener(
                { Channel(ArtemisConfiguration.centralLogQueue) },
                Executors.newSingleThreadExecutor()) {

        }

        listener.addDelegate(object : Handler<LogMessage> {
            override fun onMessage(message: LogMessage, replyChannel: Channel?) {
                log.info("${message}: ${message.logEntries.count()}")
            }
        })

        listener.start()

        // Wait for some messages to be received
        Thread.sleep(20000)

        listener.close()
    }
}

