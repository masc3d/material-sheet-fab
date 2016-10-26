package org.deku.leoz.node.prototype.sync

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.node.test.DataTest
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.MstRoute
import org.deku.leoz.node.data.sync.EntityConsumer
import org.deku.leoz.node.data.sync.EntityPublisher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import java.util.concurrent.Executors

/**
 * Created by masc on 18.06.15.
 */
//@Ignore
@RunWith(SpringJUnit4ClassRunner::class)
class EntitySyncTest : DataTest() {
    @PersistenceUnit(name = PersistenceConfiguration.QUALIFIER)
    private val entityManagerFactory: EntityManagerFactory? = null

    private var listener: EntityPublisher? = null
    private var client: EntityConsumer? = null

    @Before
    @Throws(Exception::class)
    fun setup() {
        val l = LoggerFactory.getLogger("org.deku.leoz.node") as Logger
        l.level = Level.DEBUG

        // Enforcing tcp connection
        //ActiveMQContext.instance().getBroker().setLocalUri(new URI("tcp://localhost:61616"));

        // Starting broker
        ActiveMQConfiguration.instance.broker.start()

        listener = EntityPublisher(
                notificationChannelConfiguration = ActiveMQConfiguration.Companion.instance.entitySyncTopic,
                requestChannelConfiguration = ActiveMQConfiguration.Companion.instance.entitySyncQueue,
                entityManagerFactory = entityManagerFactory!!,
                executor = Executors.newSingleThreadExecutor())

        client = EntityConsumer(
                notificationChannelConfiguration = ActiveMQConfiguration.Companion.instance.entitySyncTopic,
                requestChannelConfiguration = ActiveMQConfiguration.Companion.instance.entitySyncQueue,
                entityManagerFactory = entityManagerFactory!!,
                executor = Executors.newSingleThreadExecutor())
    }

    @After
    fun tearDown() {
        client!!.close()
        listener!!.close()
        ActiveMQConfiguration.instance.broker.close()
    }

    @Test
    @Throws(Exception::class)
    fun testSync() {
        listener!!.start()
        client!!.request(MstRoute::class.java, null, true)

        Thread.sleep(10000)
    }
}
