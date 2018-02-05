package sx.mq.jms.activemq

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.junit.PrototypeTest
import sx.mq.config.MqTestConfiguration

/**
 * Created by masc on 07.05.17.
 */
@Category(PrototypeTest::class)
class ActiveMQBrokerTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    init {
       Kodein.global.addImport(MqTestConfiguration.module)
    }

    val broker: ActiveMQBroker by Kodein.global.lazy.instance()


    /**
     * Starts and runs broker indefinitely
     */
    @Test
    fun testStartBroker() {
        this.broker.start()
        Thread.sleep(Long.MAX_VALUE)
    }
}