package sx.jms.activemq

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.command.ActiveMQQueue
import org.apache.activemq.command.ActiveMQTopic
import org.apache.activemq.jms.pool.PooledConnectionFactory
import sx.jms.Factory
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import java.net.URI
import java.net.URISyntaxException
import javax.jms.ConnectionFactory
import javax.jms.Queue
import javax.jms.Topic

/**
 * ActiveMQ JMS factory
 * Created by masc on 15/08/16.
 */
class ActiveMQFactory : Factory {
    companion object {
        @JvmStatic val instance: ActiveMQFactory by lazy { ActiveMQFactory() }
    }

    override fun createConnectionFactory(): ConnectionFactory {
        return ActiveMQConnectionFactory()
    }

    override fun createQueue(name: String): Queue {
        return ActiveMQQueue(name)
    }

    override fun createTopic(name: String): Topic {
        return ActiveMQTopic(name)
    }

    override fun createBroker(): Broker {
        return ActiveMQBroker.instance
    }

    fun createConnectionFactory(host: String,
                                port: Int,
                                transportType: Broker.TransportType,
                                user: String,
                                password: String): ConnectionFactory {
        return this.createConnectionFactory(URI.create("${transportType.toString()}://${host}:${port}"), user, password)
    }

    fun createConnectionFactory(uri: URI,
                                user: String,
                                password: String): ConnectionFactory {
        val psf = PooledConnectionFactory()
        val cf = ActiveMQConnectionFactory(
                user,
                password,
                uri)
        cf.isWatchTopicAdvisories = false
        psf.connectionFactory = cf
        psf.maxConnections = 32
        return psf
    }
}