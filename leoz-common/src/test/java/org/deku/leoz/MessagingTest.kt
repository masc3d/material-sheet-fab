package org.deku.leoz

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.ArtemisConfiguration
import org.junit.After
import org.junit.Before
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import sx.jms.artemis.ArtemisBroker

/**
 * Created by masc on 16.06.15.
 */
abstract class MessagingTest {
    /**
     * Test broker
     */
    val broker by lazy {
//        ActiveMQConfiguration.instance.broker
        val broker = ArtemisBroker()
        broker.user = Broker.User(
                userName = ArtemisConfiguration.USERNAME,
                password = ArtemisConfiguration.PASSWORD,
                groupName = "")
        broker
    }

    @Before
    @Throws(Exception::class)
    fun setup() {
        this.broker.start()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        ArtemisConfiguration.connectionFactory.destroy()
        this.broker.stop()
    }
}
