package sx.mq.jms.activemq

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.jms.pool.PooledConnectionFactory
import sx.LazyInstance
import java.net.URI
import javax.jms.Connection
import javax.jms.ConnectionFactory
import kotlin.properties.Delegates

/**
 * ActiveMQ pooled connection factory with support for updating connection properties (eg. uri/user/pw) during runtime.
 * Uses activemq's {@link org.apache.activemq.jms.pool.PooledConnectionFactor} internally,
 * which also pools sessions and message producers
 * Created by masc on 19/09/16.
 * @param uri Broker URI
 * @param username Username
 * @param password Password
 */
class ActiveMQPooledConnectionFactory(
        uri: URI,
        username: String,
        password: String) : ConnectionFactory {

    /**
     * Wrapped connection factory. Will be reset on each property update.
     */
    val connectionFactory = LazyInstance<ConnectionFactory>( {
        val psf = PooledConnectionFactory()
        val cf = ActiveMQConnectionFactory(
                this.user,
                this.password,
                this.uri)
        cf.isWatchTopicAdvisories = false
        psf.connectionFactory = cf
        psf.maxConnections = 32
        psf
    })

    /**
     */
    var uri: URI by Delegates.observable(uri, { _, _, _ ->
        this.connectionFactory.reset()
    })

    /**
     * Username
     */
    var user: String by Delegates.observable(username, { _, _, _ ->
        this.connectionFactory.reset()
    })

    /**
     * Password
     */
    var password: String by Delegates.observable(password, { _, _, _ ->
        this.connectionFactory.reset()
    })

    override fun createConnection(): Connection {
        return this.connectionFactory.get().createConnection()
    }

    override fun createConnection(userName: String?, password: String?): Connection {
        return this.connectionFactory.get().createConnection(userName, password)
    }
}