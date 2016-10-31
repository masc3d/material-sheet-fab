package org.deku.leoz.node.config

import com.google.common.base.Strings
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
import org.deku.leoz.config.ActiveMQConfiguration
import org.deku.leoz.config.ArtemisConfiguration
import org.deku.leoz.node.App
import org.deku.leoz.node.peer.RemotePeerSettings
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.inject.Named

/**
 * ActiveMQ broker configuration
 * Created by masc on 11.06.15.
 */
@Configuration
@Lazy(false)
open class MessageBrokerConfiguration {
    private val log = LoggerFactory.getLogger(MessageBrokerConfiguration::class.java)

    @Named
    @ConfigurationProperties(prefix = "broker")
    /**
     * Configuration properties
     */
    class Settings {
        var nativePort: Int? = null
        var httpContextPath: String? = null
    }

    @Inject
    private lateinit var settings: Settings

    @Inject
    private lateinit var peerSettings: RemotePeerSettings

    @PostConstruct
    fun onInitialize() {

        // Broker configuration, must occur before tunnel servlet starts
        log.info("Configuring messaging broker")
        ActiveMQBroker.instance.brokerName = "leoz-aq-${App.instance.identity.keyInstance.short}"
        ActiveMQBroker.instance.dataDirectory = StorageConfiguration.instance.activeMqDataDirectory
        ActiveMQBroker.instance.nativeTcpPort = this.settings.nativePort

        if (!Strings.isNullOrEmpty(peerSettings.hostname)) {
            // TODO: we could probe for available remote ports here, but this implies
            // init of peer brokers should also be threaded, as timeouts may occur
            log.info("Adding peer broker: ${peerSettings.hostname}")

            ActiveMQBroker.instance.addPeerBroker(Broker.PeerBroker(
                    peerSettings.hostname!!,
                    Broker.TransportType.TCP,
                    peerSettings.broker.nativePort))
        }

        // Initialize connection factory UDI
        ActiveMQConfiguration.instance.connectionFactory.uri = ActiveMQBroker.instance.localUri

        // The broker is currently started by the http tunnel servlet, initialized via web context
    }

    @PreDestroy
    fun onDestroy() {
        ActiveMQBroker.instance.close()
    }
}
