package org.deku.leoz.config.messaging

import sx.jms.Channel
import sx.jms.converters.DefaultConverter
import sx.jms.embedded.Broker
import sx.jms.embedded.activemq.ActiveMQBroker
import javax.jms.Queue
import javax.jms.Topic

/**
 * ActiveMQ specific messaging configuration
 * Created by masc on 16.04.15.
 */
class ActiveMQConfiguration private constructor() : MessagingConfiguration {

    companion object {
        val USERNAME = "leoz"
        val PASSWORD = "iUbmQRejRI1P3SNtzwIM7wAgNazURPcVcBU7SftyZ0oha9FlnAdGAmXdEQwYlKFC"
        val GROUPNAME = "leoz"

        /** Singleton instance  */
        @JvmStatic val instance by lazy({
            ActiveMQConfiguration()
        })
    }

    init {
        // Configure broker authentication
        this.broker.user = Broker.User(USERNAME, PASSWORD, GROUPNAME)
    }

    override val broker: Broker
        get() = ActiveMQBroker.instance

    override val centralQueue: Channel.Configuration by lazy({
        Channel.Configuration(
                connectionFactory = this.broker.connectionFactory,
                destination = this.broker.createQueue("leoz.central.queue"),
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    })

    override val centralLogQueue: Channel.Configuration by lazy {
        val c = Channel.Configuration(
                connectionFactory = this.broker.connectionFactory,
                destination = this.broker.createQueue("leoz.log.queue"),
                deliveryMode = Channel.DeliveryMode.Persistent,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))

        c.priority = 1
        c
    }

    override val entitySyncQueue: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.broker.connectionFactory,
                destination = this.broker.createQueue("leoz.entity-sync.queue"),
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    override val entitySyncTopic: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.broker.connectionFactory,
                destination = this.broker.createTopic("leoz.entity-sync.topic"),
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    override fun nodeQueue(id: Int): Channel.Configuration {
        return Channel.Configuration(connectionFactory = this.broker.connectionFactory,
                destination = this.broker.createQueue("leoz.node.queue." + id.toString()),
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    override val nodeNotificationTopic: Channel.Configuration by lazy {
        Channel.Configuration(connectionFactory = this.broker.connectionFactory,
                destination = this.broker.createTopic("leoz.node.notification.topic"),
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }
}
