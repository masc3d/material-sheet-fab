package org.deku.leoz.node.sync

import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.ArtemisConfiguration
import org.deku.leoz.node.config.DataTestConfiguration
import org.deku.leoz.node.config.MessageBrokerTestConfiguration
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.service.internal.sync.EntityConsumer
import org.deku.leoz.node.service.internal.sync.EntityPublisher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.artemis.ArtemisBroker
import sx.junit.PrototypeTest
import sx.mq.jms.JmsChannel
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit

/**
 * Created by masc on 18.06.15.
 */
//@Ignore
@Category(PrototypeTest::class)
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(
        DataTestConfiguration::class,
        MessageBrokerTestConfiguration::class))
class EntitySyncTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private lateinit var entityManagerFactory: EntityManagerFactory

    private var listener: EntityPublisher? = null
    private var client: EntityConsumer? = null

    @Inject
    private lateinit var broker: MqBroker

    @Before
    @Throws(Exception::class)
    fun setup() {
        // Enforcing tcp connection
        //ActiveMQContext.instance().getBroker().setLocalUri(new URI("tcp://localhost:61616"));

        // Starting broker
        this.broker.start()

        val notificationChannelConfig: JmsChannel
        val requestChannelConfig: JmsChannel
        when (this.broker) {
            is ActiveMQBroker -> {
                notificationChannelConfig = ActiveMQConfiguration.entitySyncTopic
                requestChannelConfig = ActiveMQConfiguration.entitySyncQueue
            }
            is ArtemisBroker -> {
                notificationChannelConfig = ArtemisConfiguration.entitySyncTopic
                requestChannelConfig = ArtemisConfiguration.entitySyncQueue
            }
            else -> throw UnsupportedOperationException("Unknown broker type")
        }

        listener = EntityPublisher(
                notificationChannel = notificationChannelConfig,
                requestChannel = requestChannelConfig,
                entityManagerFactory = entityManagerFactory,
                listenerExecutor = Executors.newSingleThreadExecutor())

        client = EntityConsumer(
                notificationChannel = notificationChannelConfig,
                requestChannel = requestChannelConfig,
                entityManagerFactory = entityManagerFactory,
                listenerExecutor = Executors.newSingleThreadExecutor())
    }

    @After
    fun tearDown() {
        client!!.close()
        listener!!.close()
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
