package org.deku.leoz.messaging

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.apache.activemq.broker.region.virtual.CompositeTopic
import org.apache.activemq.command.ActiveMQQueue
import org.deku.leoz.config.MessagingTestConfiguration
import org.junit.Test
import org.slf4j.LoggerFactory
import sx.jms.activemq.ActiveMQBroker

/**
 * Created by masc on 07.05.17.
 */
class BrokerTest {
    val log = LoggerFactory.getLogger(this.javaClass)

    init {
        Kodein.global.addImport(MessagingTestConfiguration.module)
    }

    val broker: ActiveMQBroker by Kodein.global.lazy.instance()


    @Test
    fun testStartBroker() {
        this.broker.start()
        Thread.sleep(Long.MAX_VALUE)
    }
}