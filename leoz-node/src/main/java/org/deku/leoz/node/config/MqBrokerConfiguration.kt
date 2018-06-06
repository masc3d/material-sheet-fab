package org.deku.leoz.node.config

import com.google.common.base.Strings
import org.apache.activemq.broker.region.virtual.CompositeTopic
import org.deku.leoz.config.JmsConfiguration
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.config.MqEndpoints
import org.deku.leoz.identity.Identity
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
class MqBrokerConfiguration {
    private val log = LoggerFactory.getLogger(MqBrokerConfiguration::class.java)

    /**
     * Configuration properties
     */
    @Configuration
    @ConfigurationProperties(prefix = "broker")
    class Settings {
        var nativePort: Int? = null
        var httpContextPath: String? = null
    }

    @Inject
    private lateinit var identity: Identity

    @Inject
    private lateinit var settings: Settings

    @Inject
    private lateinit var peerSettings: RemotePeerConfiguration

    @Inject
    private lateinit var storage: Storage

    @PostConstruct
    fun onInitialize() {

        val broker = JmsConfiguration.broker

        // Broker configuration, must occur before tunnel servlet starts
        log.info("Configuring messaging broker")

        broker.brokerName = "leoz-aq-${identity.uid.short}"
        broker.dataDirectory = storage.activeMqDataDirectory
        broker.nativeTcpPort = this.settings.nativePort

        if (!Strings.isNullOrEmpty(peerSettings.host)) {
            // TODO: we could probe for available remote ports here, but this implies
            // init of peer brokers should also be threaded, as timeouts may occur
            log.info("Adding peer broker: ${peerSettings.host}")

            broker.addPeerBroker(MqBroker.PeerBroker(
                    peerSettings.host!!,
                    MqBroker.TransportType.TCP,
                    peerSettings.broker.nativePort))
        }

        // Initialize connection factory UDI
        JmsConfiguration.connectionFactory.uri = broker.localUri

        // Setup composite destinations for mqtt destinations

        // REMARK: MQTT doesn't support queues natively, thus using topics as virtual endpoints
        // which are forwarded to queues internally.

        broker.addCompositeDestination({
            CompositeTopic().also {
                it.name = MqEndpoints.central.main.mqtt.kryo.destinationName
                it.forwardTo = listOf(
                        JmsEndpoints.central.main.kryo.destination)
            }
        }())

        broker.addCompositeDestination({
            CompositeTopic().also {
                it.name = MqEndpoints.central.transient.mqtt.kryo.destinationName
                it.forwardTo = listOf(
                        JmsEndpoints.central.transient.kryo.destination)
            }
        }())

        // Setup composite destinations for node destinations

        broker.addCompositeDestination({
            CompositeTopic().also {
                it.name = MqEndpoints.node.main(identity.uid).kryo.destinationName
                it.isForwardOnly = false
                it.forwardTo = listOf(
                        JmsEndpoints.central.main.kryo.destination)
            }
        }())

        broker.addCompositeDestination({
            CompositeTopic().also {
                it.name = MqEndpoints.node.transient(identity.uid).kryo.destinationName
                it.isForwardOnly = false
                it.forwardTo = listOf(
                        JmsEndpoints.central.transient.kryo.destination)
            }
        }())

        // The broker is currently started by the http tunnel servlet, initialized via web context
    }

    @get:Bean
    val activeMqConfiguration
        get() = JmsConfiguration

    @get:Bean
    val activeMqBroker
        get() = ActiveMQBroker.instance

    @PreDestroy
    fun onDestroy() {
        ActiveMQBroker.instance.close()
    }
}
