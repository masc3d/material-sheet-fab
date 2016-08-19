package org.deku.leoz.log

import ch.qos.logback.classic.Logger
import org.deku.leoz.Identity
import org.deku.leoz.MessagingTest
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.Bundles
import org.deku.leoz.config.messaging.ActiveMQConfiguration
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
    private val mLog = LoggerFactory.getLogger(this.javaClass)

    @Test
    @Throws(JMSException::class)
    fun testSend() {
        // Setup log appender
        val lAppender = LogAppender(
                messagingConfiguration = ActiveMQConfiguration.instance,
                idenitySupplier = { Identity.create(Bundles.LEOZ_NODE.value, SystemInformation.create()) })
        lAppender.start()
        val lRoot = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        lRoot.addAppender(lAppender)

        // Generate some log messages
        for (i in 0..99)
            lRoot.info("Test!")

        // Dispose to make sure everything is flushed
        lAppender.close()
    }

    @Test
    @Throws(JMSException::class, InterruptedException::class)
    fun testReceive() {
        // Setup log message listener
        val mListener = object : SpringJmsListener(
                { Channel(ActiveMQConfiguration.instance.centralLogQueue) },
                Executors.newSingleThreadExecutor()) {

        }

        mListener.addDelegate(LogMessage::class.java, object : Handler<LogMessage> {
            override fun onMessage(message: LogMessage, replyChannel: Channel?) {
                mLog.info(message)
            }
        })

        mListener.start()

        // Wait for some messages to be received
        Thread.sleep(20000)

        mListener.close()
    }
}

