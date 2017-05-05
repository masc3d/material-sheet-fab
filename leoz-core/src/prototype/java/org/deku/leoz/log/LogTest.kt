package org.deku.leoz.log

import ch.qos.logback.classic.Logger
import org.deku.leoz.identity.Identity
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.identity.DesktopIdentityFactory
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.jms.Channel
import sx.jms.Handler
import sx.jms.listeners.SpringJmsListener

import javax.jms.JMSException
import java.util.concurrent.Executors

/**
 * @author masc
 */
class LogTest : org.deku.leoz.MessagingTest() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    @Throws(JMSException::class)
    fun testSend() {
        // Setup log appender
        val logAppender = LogAppender(
                broker = this.broker,
                logChannelConfiguration= ActiveMQConfiguration.Companion.instance.centralLogQueue,
                identitySupplier = {
                    DesktopIdentityFactory(BundleType.LEOZ_NODE.value, SystemInformation.Companion.create()).create()
                })
        logAppender.start()

        val logRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        logRoot.addAppender(logAppender)

        // Generate some log messages
        for (i in 0..10000)
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
                { Channel(ActiveMQConfiguration.Companion.instance.centralLogQueue) },
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

