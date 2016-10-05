package org.deku.leoz

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.deku.leoz.config.ActiveMQConfiguration
import org.junit.After
import org.junit.Before
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker

/**
 * Created by masc on 16.06.15.
 */
abstract class MessagingTest {
    /**
     * Test broker
     */
    val broker by lazy {
        ActiveMQConfiguration.instance.broker
    }

    @Before
    @Throws(Exception::class)
    fun setup() {
        this.broker.start()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        this.broker.stop()
    }
}
