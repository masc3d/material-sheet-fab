package org.deku.leoz.log

import ch.qos.logback.classic.Logger
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.SystemInformation
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.config.MessagingTestConfiguration
import org.deku.leoz.identity.DesktopIdentityFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.channel
import sx.mq.jms.listeners.SpringJmsListener

import javax.jms.JMSException
import java.util.concurrent.Executors

/**
 * @author masc
 */
class LogTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        Kodein.global.addImport(MessagingTestConfiguration.module)
    }

    val broker: ActiveMQBroker by Kodein.global.lazy.instance()

    @Before
    fun setup() {
        this.broker.start()
    }

    @After
    fun tearDown() {
        this.broker.stop()
    }

    @Test
    @Throws(JMSException::class)
    fun testSend() {
        // Setup log appender
        val logAppender = LogMqAppender(
                channelSupplier = { JmsEndpoints.central.transient.kryo.channel() },
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
                JmsEndpoints.central.transient.kryo,
                Executors.newSingleThreadExecutor()) {

        }

        listener.addDelegate(object : MqHandler<LogMessage> {
            override fun onMessage(message: LogMessage, replyChannel: MqChannel?) {
                log.info("${message}: ${message.logEntries.count()}")
            }
        })

        listener.start()

        // Wait for some messages to be received
        Thread.sleep(20000)

        listener.close()
    }
}

