package sx.mq.jms.artemis

import org.apache.activemq.artemis.api.core.TransportConfiguration
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient
import org.apache.activemq.artemis.api.jms.JMSFactoryType
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants
import org.junit.Test
import org.junit.experimental.categories.Category
import org.springframework.jms.connection.CachingConnectionFactory
import sx.junit.PrototypeTest
import sx.mq.MqBroker
import java.io.File

/**
 * Created by masc on 05/10/2016.
 */
@Category(PrototypeTest::class)
class ArtemisBrokerTest {
    val BROKER_USERNAME = "admin"
    val BROKER_PASWORD = "admin"
    val BROKER_PORT = 61616
    val BROKER_PEER = "10.211.55.13"

    /**
     * Broker instance for testing
     */
    private val broker by lazy {
        val broker = ArtemisBroker()
        broker.user = MqBroker.User(
                userName = BROKER_USERNAME,
                password = BROKER_PASWORD,
                groupName = "")
        broker.dataDirectory = File("build/artemis")
        broker
    }

    @Test
    fun testBrokerWithPeerIndefinitely() {
        this.broker.addPeerBroker(
                MqBroker.PeerBroker(BROKER_PEER, MqBroker.TransportType.TCP, BROKER_PORT)
        )
        this.broker.start()
        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun testBrokerConnection() {
        val transportParams = mutableMapOf<String, Any>()
        transportParams.put(TransportConstants.HOST_PROP_NAME, "localhost")
        transportParams.put(TransportConstants.PORT_PROP_NAME, 61616)

        val transportConfiguration = TransportConfiguration(
                NettyConnectorFactory::class.java.getName(),
                transportParams)

        val cf = ActiveMQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration)
                .setUser(this.broker.user!!.userName)
                .setPassword(this.broker.user!!.password)

        val ccf = CachingConnectionFactory(cf)
        val c = ccf.createConnection()
        c.close()

        ccf.destroy()
    }
}