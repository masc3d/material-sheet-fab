package sx.jms.artemis

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient
import org.apache.activemq.artemis.api.jms.JMSFactoryType
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants
import org.junit.Test
import org.springframework.jms.connection.CachingConnectionFactory
import sx.jms.Broker

/**
 * Created by masc on 05/10/2016.
 */
class ArtemisBrokerTest {
    /**
     * Broker instance for testing
     */
    private val broker by lazy {
        val broker = ArtemisBroker()
        broker.user = Broker.User(
                userName = "admin",
                password = "admin",
                groupName = "")
        broker
    }

    @Test
    fun testBroker() {
        this.broker.start()

        val transportParams = mutableMapOf<String, Any>()
        transportParams.put(TransportConstants.HOST_PROP_NAME, "0.0.0.0")
        transportParams.put(TransportConstants.PORT_PROP_NAME, 61616)

        val transportConfiguration = TransportConfiguration(NettyConnectorFactory::class.java.getName(), transportParams)

        val cf = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration)
                .setUser(this.broker.user!!.userName)
                .setPassword(this.broker.user!!.password)

        val ccf = CachingConnectionFactory(cf)
        val c = ccf.createConnection()
        c.close()

        ccf.destroy()

//        Thread.sleep(5000)
        this.broker.stop()
    }
}