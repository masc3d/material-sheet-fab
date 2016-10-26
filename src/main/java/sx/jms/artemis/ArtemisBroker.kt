package sx.jms.artemis

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl
import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants
import org.apache.activemq.artemis.core.security.Role
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ
import org.apache.activemq.artemis.jms.server.config.impl.ConnectionFactoryConfigurationImpl
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManagerImpl
import sx.jms.Broker
import java.util.*

/**
 * Created by masc on 05/10/2016.
 */
class ArtemisBroker : Broker(NATIVE_TCP_PORT) {
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
        val nettyAcceptorParams = mutableMapOf<String, Any>()
        nettyAcceptorParams.put(TransportConstants.HOST_PROP_NAME, "0.0.0.0")
        nettyAcceptorParams.put(TransportConstants.PORT_PROP_NAME, 61616)

        // Setup security/authentication
        val adminRole = Role("admin", true, true, true, true, true, true, true, true)

        // ActiveMQSecurityManagerImpl is deprecated but works fine for our needs for now.
        // TODO: Make this work with ActiveMQJAASSecurityManager (requires a `SecurityConfiguration` and some kind of JAAS `LoginModule`, eg `PropertiesLoginModule`
        val securityManager = ActiveMQSecurityManagerImpl()

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
                                NettyAcceptorFactory::class.java.getName(), nettyAcceptorParams))

//                .addConnectorConfiguration("myConnector",
//                        TransportConfiguration(
//                                NettyAcceptorFactory::class.java.getName()))


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