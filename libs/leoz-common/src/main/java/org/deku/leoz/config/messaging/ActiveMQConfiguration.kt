package org.deku.leoz.config.messaging

import org.deku.leoz.Identity
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.jms.Channel
import sx.jms.converters.DefaultConverter
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import sx.jms.activemq.ActiveMQFactory
import javax.jms.ConnectionFactory

/**
 * ActiveMQ specific messaging configuration
 * @param connectionFactory Optional connection factory. If not set reverts to the local broker URI.
 * Created by masc on 16.04.15.
 */
class ActiveMQConfiguration(connectionFactory: ConnectionFactory? = null) : MessagingConfiguration {

    companion object {
        // Leoz broker configuration only has a single user which is defined here
        val USERNAME = "leoz"
        val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"
        val GROUPNAME = "leoz"

        /**
         * Singleton instance of a messaging configuration accessing a local/embedded broker
         */
        @JvmStatic val instance by lazy({
            ActiveMQConfiguration()
        })
    }

    private var _connectionFactory: ConnectionFactory?

    init {
        // Configure broker authentication
        this.broker.user = Broker.User(USERNAME, PASSWORD, GROUPNAME)
        _connectionFactory = connectionFactory
    }

    /**
     * Connection factory
     */
    val connectionFactory by lazy {
        _connectionFactory ?: this.broker.connectionFactory
    }

    override val broker: Broker
        get() = ActiveMQBroker.instance

    override val centralQueue: Channel.Configuration by lazy({
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQFactory.instance.createQueue("leoz.central.queue"),
                converter = DefaultConverter(KryoSerializer().gzip))
    })

    override val centralLogQueue: Channel.Configuration by lazy {
        val c = Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQFactory.instance.createQueue("leoz.log.queue"),
                deliveryMode = Channel.DeliveryMode.Persistent,
                converter = DefaultConverter(KryoSerializer().gzip))

        c.priority = 1
        c
    }

    override val entitySyncQueue: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQFactory.instance.createQueue("leoz.entity-sync.queue"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    override val entitySyncTopic: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQFactory.instance.createTopic("leoz.entity-sync.topic"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    override fun nodeQueue(identityKey: Identity.Key): Channel.Configuration {
        return Channel.Configuration(connectionFactory = this.connectionFactory,
                destination = ActiveMQFactory.instance.createQueue("leoz.node.queue." + identityKey.short),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    override val nodeNotificationTopic: Channel.Configuration by lazy {
        Channel.Configuration(connectionFactory = this.connectionFactory,
                destination = ActiveMQFactory.instance.createTopic("leoz.node.notification.topic"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }
}
