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
import java.io.File

/**
 * Created by masc on 16.06.15.
 */
abstract class MessagingTest {
    /**
     * Test broker
     */
    val broker by lazy {
        val broker = ActiveMQConfiguration.instance.broker
        broker.user = Broker.User(
                userName = ActiveMQConfiguration.USERNAME,
                password = ActiveMQConfiguration.PASSWORD,
                groupName = ActiveMQConfiguration.GROUPNAME)
        broker.dataDirectory = File("build/activemq")
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
        this.broker.stop()
    }
}
