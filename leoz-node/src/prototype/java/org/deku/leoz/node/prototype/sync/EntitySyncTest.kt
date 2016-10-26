package org.deku.leoz.node.prototype.sync

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.ArtemisConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.data.sync.EntityConsumer
import org.deku.leoz.node.data.sync.EntityPublisher
import org.deku.leoz.node.test.DataTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import sx.jms.Channel
import java.util.concurrent.Executors
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Created by masc on 18.06.15.
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
class EntitySyncTest : DataTest() {
    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    private var listener: EntityPublisher? = null
    private var client: EntityConsumer? = null

    private val USE_ARTEMIS = true

    @Before
    @Throws(Exception::class)
    fun setup() {
        val l = LoggerFactory.getLogger("org.deku.leoz.node") as Logger
        l.level = Level.DEBUG

        // Enforcing tcp connection
        //ActiveMQContext.instance().getBroker().setLocalUri(new URI("tcp://localhost:61616"));

        // Starting broker
        if (!USE_ARTEMIS)
            ActiveMQConfiguration.instance.broker.start()
        else
            ArtemisConfiguration.broker.start()

        val notificationChannelConfig: Channel.Configuration
        val requestChannelConfig: Channel.Configuration
        if (!USE_ARTEMIS) {
            notificationChannelConfig = ActiveMQConfiguration.instance.entitySyncTopic
            requestChannelConfig = ActiveMQConfiguration.instance.entitySyncQueue
        } else {
            notificationChannelConfig = ArtemisConfiguration.entitySyncTopic
            requestChannelConfig = ArtemisConfiguration.entitySyncQueue
        }

        listener = EntityPublisher(
                notificationChannelConfiguration = notificationChannelConfig,
                requestChannelConfiguration = requestChannelConfig,
                entityManagerFactory = entityManagerFactory,
                executor = Executors.newSingleThreadExecutor())

        client = EntityConsumer(
                notificationChannelConfiguration = notificationChannelConfig,
                requestChannelConfiguration = requestChannelConfig,
                entityManagerFactory = entityManagerFactory,
                executor = Executors.newSingleThreadExecutor())
    }

    @After
    fun tearDown() {
        client!!.close()
        listener!!.close()
        if (!USE_ARTEMIS) {
            ActiveMQConfiguration.instance.broker.close()
        } else {
            (ArtemisConfiguration.connectionFactory.targetConnectionFactory as ActiveMQConnectionFactory).close()
            ArtemisConfiguration.broker.close()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSync() {
        listener!!.start()
        for (i in 0..8)
            client!!.request(MstRoute::class.java, null, true)

        Thread.sleep(60000)
    }
}
