package org.deku.leoz.node.config

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.deku.leoz.config.ArtemisConfiguration
import org.deku.leoz.config.JmsConfiguration
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import java.io.File
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * ActiveMQ broker configuration
 * Created by masc on 11.06.15.
 */
@Configuration
@Lazy(false)
class MessageBrokerTestConfiguration {
    private val log = LoggerFactory.getLogger(MessageBrokerTestConfiguration::class.java)

    private val USE_ARTEMIS = false

    @PostConstruct
    fun onInitialize() {
        // Broker configuration, must occur before tunnel servlet starts
        log.info("Configuring messaging broker")
        ActiveMQBroker.instance.brokerName = "leoz-aq-test"
        ActiveMQBroker.instance.dataDirectory = File("build/activemq")

        // Initialize connection factory UDI
        JmsConfiguration.connectionFactory.uri = ActiveMQBroker.instance.localUri
    }

    @get:Bean
    val broker: MqBroker = ActiveMQBroker.instance

    @PreDestroy
    fun onDestroy() {
        if (USE_ARTEMIS) {
            (ArtemisConfiguration.connectionFactory.targetConnectionFactory as ActiveMQConnectionFactory).close()
        }
        this.broker.close()
    }
}
