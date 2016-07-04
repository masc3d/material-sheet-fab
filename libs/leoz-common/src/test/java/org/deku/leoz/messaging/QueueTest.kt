package org.deku.leoz.messaging

import org.deku.leoz.MessagingTest
import org.deku.leoz.config.messaging.ActiveMQConfiguration
import org.junit.Test
import org.slf4j.LoggerFactory
import javax.jms.DeliveryMode

/**
 * Created by masc on 20/02/16.
 */
class QueueTest : MessagingTest() {
    val log = LoggerFactory.getLogger(this.javaClass)
    val configuration = ActiveMQConfiguration.instance

    @Test
    fun testTemporaryQueueSendReceiveWithTransaction() {
        val cn = this.configuration.broker.connectionFactory.createConnection()
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
        Thread.sleep(Long.MAX_VALUE)
    }
}