package org.deku.leoz.messaging

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.MessagingTestConfiguration
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.mq.jms.activemq.ActiveMQBroker
import javax.jms.DeliveryMode
import sx.logging.slf4j.*

/**
 * Created by masc on 20/02/16.
 */
class JmsTest {
    val log = LoggerFactory.getLogger(this.javaClass)

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
    fun testTemporaryQueueSendReceiveWithTransaction() {
        val cn = ActiveMQConfiguration.instance.connectionFactory.createConnection()
        cn.start()
        val s = cn.createSession(true, -1)
        val q = s.createTemporaryQueue()
        val mp = s.createProducer(q)
        mp.deliveryMode = DeliveryMode.NON_PERSISTENT
        mp.send(s.createTextMessage("hello"))
        s.commit()

        val s2 = cn.createSession(true, -1)
        val mc = s2.createConsumer(q)
        val msg = mc.receive()
        //        msg.acknowledge()
        log.info(msg)

        s2.commit()
        mc.close()

        s2.close()
        s.close()
        q.delete()

        // TODO. verify that temp queue doesn't have pending entries (bug with activemq upto 5.13.1 when using temp queues within transaction)
//        Thread.sleep(Long.MAX_VALUE)
    }
}