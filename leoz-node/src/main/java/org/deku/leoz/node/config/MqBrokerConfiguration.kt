package org.deku.leoz.node.config

import com.google.common.base.Strings
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.node.Application
import org.deku.leoz.node.Storage
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

/**
 * ActiveMQ broker configuration
 * Created by masc on 11.06.15.
 */
@Configuration
@Lazy(false)
open class MqBrokerConfiguration {
    private val log = LoggerFactory.getLogger(MqBrokerConfiguration::class.java)

    /**
     * Configuration properties
     */
    @Configuration
    @ConfigurationProperties(prefix = "broker")
    open class Settings {
        var nativePort: Int? = null
        var httpContextPath: String? = null
    }

    @Inject
    private lateinit var application: Application
    @Inject
    private lateinit var settings: Settings
    @Inject
    private lateinit var peerSettings: RemotePeerConfiguration
    @Inject
    private lateinit var storage: Storage

    @PostConstruct
    fun onInitialize() {

        // Broker configuration, must occur before tunnel servlet starts
        log.info("Configuring messaging broker")
        ActiveMQBroker.instance.brokerName = "leoz-aq-${this.application.identity.uid.short}"
        ActiveMQBroker.instance.dataDirectory = storage.activeMqDataDirectory
        ActiveMQBroker.instance.nativeTcpPort = this.settings.nativePort

        if (!Strings.isNullOrEmpty(peerSettings.host)) {
            // TODO: we could probe for available remote ports here, but this implies
            // init of peer brokers should also be threaded, as timeouts may occur
            log.info("Adding peer broker: ${peerSettings.host}")

            ActiveMQBroker.instance.addPeerBroker(MqBroker.PeerBroker(
                    peerSettings.host!!,
                    MqBroker.TransportType.TCP,
                    peerSettings.broker.nativePort))
        }

        // Initialize connection factory UDI
        JmsConfiguration.connectionFactory.uri = ActiveMQBroker.instance.localUri

        // The broker is currently started by the http tunnel servlet, initialized via web context
    }

    @get:Bean
    open val activeMqConfiguration
        get() = JmsConfiguration

    @get:Bean
    open val activeMqBroker
        get() = ActiveMQBroker.instance

    @PreDestroy
    fun onDestroy() {
        ActiveMQBroker.instance.close()
    }
}
