package org.deku.leoz

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.junit.After
import org.junit.Before
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker

/**
 * Created by masc on 16.06.15.
 */
abstract class MessagingTest {
    private val httpUrl = "http://localhost:8080/leoz/jms"
    //private String mNativeUrl = "tcp://localhost:61616";
    private val nativeUrl = "vm://localhost?create=false"

    private var broker: Broker? = null

    @Before
    @Throws(Exception::class)
    fun setup() {
        // Log levels
        val root = org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = Level.INFO

        val lMessaging = org.slf4j.LoggerFactory.getLogger("org.deku.leoz.messaging") as Logger
        lMessaging.level = Level.TRACE

        // Start broker
        broker = ActiveMQBroker.instance
        broker!!.brokerName = "localhost"
        broker!!.start()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        broker!!.stop()
    }
}
