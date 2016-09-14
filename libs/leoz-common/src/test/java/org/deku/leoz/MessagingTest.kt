package org.deku.leoz

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.junit.After
import org.junit.Before
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker

/**
 * Created by masc on 16.06.15.
 */
abstract class MessagingTest {
    @Before
    @Throws(Exception::class)
    fun setup() {
        // Log levels
        val root = org.slf4j.LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = Level.DEBUG

        val lMessaging = org.slf4j.LoggerFactory.getLogger("org.deku.leoz.messaging") as Logger
        lMessaging.level = Level.TRACE

        // Start broker
        ActiveMQConfiguration.instance.broker.start()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ActiveMQConfiguration.instance.broker.stop()
    }
}
