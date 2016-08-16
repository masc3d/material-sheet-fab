package org.deku.leoz.node.config

import com.google.common.base.Strings
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

/**
 * ActiveMQ broker configuration
 * Created by masc on 11.06.15.
 */
@Configuration
@ConfigurationProperties(prefix = "broker")
@Lazy(false)
open class MessageBrokerConfiguration {
    private val log = LoggerFactory.getLogger(MessageBrokerConfiguration::class.java)

    @Inject
    private lateinit var peerSettings: RemotePeerSettings

    // Configuration properties
    var nativePort: Int? = null
    var httpContextPath: String? = null

    @PostConstruct
    fun onInitialize() {

        // Broker configuration, must occur before tunnel servlet starts
        log.info("Configuring messaging broker")
        ActiveMQBroker.instance.brokerName = "leoz-aq-${App.instance.identity.keyInstance.short}"
        ActiveMQBroker.instance.dataDirectory = StorageConfiguration.instance.activeMqDataDirectory
        ActiveMQBroker.instance.nativeTcpPort = this.nativePort

        if (!Strings.isNullOrEmpty(peerSettings.hostname)) {
            // TODO: we could probe for available remote ports here, but this implies
            // init of peer brokers should also be threaded, as timeouts may occur
            log.info("Adding peer broker: ${peerSettings.hostname}")

            ActiveMQBroker.instance.addPeerBroker(Broker.PeerBroker(
                    peerSettings.hostname!!,
                    Broker.TransportType.TCP,
                    peerSettings.broker.nativePort))
        }

        // The broker is currently started by the http tunnel servlet, initialized via web context
    }

    @PreDestroy
    fun onDestroy() {
        ActiveMQBroker.instance.close()
    }
}
