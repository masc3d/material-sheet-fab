package sx.mq.jms.artemis

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.core.config.ClusterConnectionConfiguration
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants
import org.apache.activemq.artemis.core.security.Role
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ
import org.apache.activemq.artemis.core.settings.impl.AddressSettings
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS
import sx.mq.MqBroker

/**
 * Created by masc on 05/10/2016.
 */
class ArtemisBroker : MqBroker(NATIVE_TCP_PORT) {
    companion object {
        // Defaults
        private val NATIVE_TCP_PORT = 61616
    }

    /**
     * Artemis embedded server
     */
    private var server: EmbeddedActiveMQ? = null

    override fun startImpl() {
        // Setup acceptors
        val brokerAcceptorParams = mutableMapOf<String, Any>()
        brokerAcceptorParams.put(TransportConstants.HOST_PROP_NAME, "0.0.0.0")
        brokerAcceptorParams.put(TransportConstants.PORT_PROP_NAME, this.nativeTcpPort ?: NATIVE_TCP_PORT)
        brokerAcceptorParams.put(TransportConstants.USE_NIO_PROP_NAME, true)

        val brokerConnectorParams = mutableMapOf<String, Any>()
        brokerConnectorParams.put(TransportConstants.HOST_PROP_NAME, "192.168.0.215")
        brokerConnectorParams.put(TransportConstants.PORT_PROP_NAME, this.nativeTcpPort ?: NATIVE_TCP_PORT)
        val BROKER_CONNECTOR_NAME = "broker-connector"

        // Setup security/authentication
        val adminRole = Role("admin", true, true, true, true, true, true, true, true)

        // ActiveMQSecurityManagerImpl is deprecated but works fine for our needs for now.
        // TODO: Make this work with ActiveMQJAASSecurityManager (requires a `SecurityConfiguration` and some kind of JAAS `LoginModule`, eg `PropertiesLoginModule`
        @Suppress("DEPRECATION")
        val securityManager = org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManagerImpl()

        val brokerUser = this.user!!
        securityManager.configuration.addUser(brokerUser.userName, brokerUser.password)
        securityManager.configuration.addRole(brokerUser.userName, adminRole.name)

        // Setup core configuration
        val configuration = ConfigurationImpl()
                .setPersistenceEnabled(true)
                .setSecurityEnabled(true)
                .putSecurityRoles("#", setOf(adminRole))
                .addAcceptorConfiguration(
                        TransportConfiguration(
                                NettyAcceptorFactory::class.java.getName(), brokerAcceptorParams))

                .addConnectorConfiguration(BROKER_CONNECTOR_NAME,
                        TransportConfiguration(
                                NettyConnectorFactory::class.java.getName(), brokerConnectorParams))

        val dataDirectory = this.dataDirectory
        if (dataDirectory != null) {
            System.setProperty("artemis.instance", dataDirectory.toString())
        }

        // Setup cluster/peer connectivity
        val staticConnectors = this.peerBrokers.map {
            val peerConnectorParams = mutableMapOf<String, Any>()
            peerConnectorParams.put(TransportConstants.HOST_PROP_NAME, it.hostname)
            peerConnectorParams.put(TransportConstants.PORT_PROP_NAME, it.port ?: NATIVE_TCP_PORT)

            val PEER_CONNECTOR_NAME = "connector.peer.${it.hostname}"
            configuration
                    .addConnectorConfiguration(PEER_CONNECTOR_NAME,
                            TransportConfiguration(
                                    NettyConnectorFactory::class.java.getName(),
                                    peerConnectorParams)
                    )
            PEER_CONNECTOR_NAME
        }

        configuration.addClusterConfiguration(
                ClusterConnectionConfiguration()
                        .setName("leoz-cluster")
                        .setConnectorName(BROKER_CONNECTOR_NAME)
                        .setStaticConnectors(staticConnectors)
                //.setAllowDirectConnectionsOnly(true)
        )

        configuration.addAddressesSetting("jms.#",
                AddressSettings()
                        .setRedistributionDelay(0))

        // Setup JMS configuration
        val jmsConfig = JMSConfigurationImpl()

//        // JMS ConnectionFactory
//        val cfConfig = ConnectionFactoryConfigurationImpl()
//                .setName("cf")
//                .setConnectorNames(Arrays.asList("myConnector"))
//                .setBindings("/cf")

//        jmsConfig.getConnectionFactoryConfigurations().add(cfConfig)

        this.server = EmbeddedJMS()
                .setConfiguration(configuration)
                .setJmsConfiguration(jmsConfig)
                .setSecurityManager(securityManager)
                .start()
    }

    override fun stopImpl() {
        val server = this.server
        if (server != null) {
            server.stop()
            this.server = null;
        }
    }

    override val isStartedImpl: Boolean
        get() = this.server?.activeMQServer?.isStarted ?: false
}