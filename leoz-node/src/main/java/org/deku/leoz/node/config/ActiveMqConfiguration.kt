package org.deku.leoz.node.config

import com.google.common.base.Strings
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.deku.leoz.node.peer.RemotePeerSettings
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker

import javax.annotation.PostConstruct
import javax.inject.Inject

/**
 * ActiveMQ broker configuration
 * Created by masc on 11.06.15.
 */
@Configuration
@ConfigurationProperties(prefix = "broker")
@Lazy(false)
open class ActiveMqConfiguration {
    private val log = LogFactory.getLog(ActiveMqConfiguration::class.java)

    @Inject
    private lateinit var peerSettings: RemotePeerSettings

    // Configuration properties
    var nativePort: Int? = null
    var httpContextPath: String? = null

    @PostConstruct
    @Throws(Exception::class)
    fun initialize() {

        //region Setup message broker
        // Broker configuration, must occur before tunnel servlet starts
        log.info("Configuring messaging broker")
        ActiveMQBroker.instance().nativeTcpPort = this.nativePort

        if (!Strings.isNullOrEmpty(peerSettings.host)) {
            // TODO: we could probe for available remote ports here, but this implies
            // init of peer brokers should also be threaded, as timeouts may occur
            log.info("Adding peer broker: ${peerSettings.host}")

            ActiveMQBroker.instance().addPeerBroker(Broker.PeerBroker(
                    peerSettings.host,
                    Broker.TransportType.TCP,
                    peerSettings.broker.nativePort))
        }
        //endregion
    }
}
