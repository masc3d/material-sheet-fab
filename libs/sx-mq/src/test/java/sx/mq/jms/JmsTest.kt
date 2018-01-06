package sx.mq.jms

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.junit.After
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.log.slf4j.info
import sx.mq.MqBroker
import sx.mq.config.MqTestConfiguration
import javax.jms.ConnectionFactory
import javax.jms.DeliveryMode

/**
 * Created by masc on 20/02/16.
 */
class JmsTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    init {
        Kodein.global.addImport(MqTestConfiguration.module)
    }

    val broker: MqBroker by Kodein.global.lazy.instance()
    val connectionFactory: ConnectionFactory by Kodein.global.lazy.instance()

    @org.junit.Before
    fun setup() {
        this.broker.start()
    }

    @After
    fun tearDown() {
        this.broker.stop()
    }

    @Test
    fun testTemporaryQueueSendReceiveWithTransaction() {
        val cn = connectionFactory.createConnection()
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