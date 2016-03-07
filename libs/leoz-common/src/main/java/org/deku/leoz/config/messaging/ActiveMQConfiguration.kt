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

    private val centralQueue: Queue by lazy({
        this.broker.createQueue("leoz.central.queue")
    })

    private val centralEntitySyncQueue: Queue by lazy({
        this.broker.createQueue("leoz.entity-sync.queue")
    })

    private val nodeEntitySyncTopic: Topic by lazy({
        this.broker.createTopic("leoz.entity-sync.topic")
    })

    private val centralLogQueue: Queue by lazy({
        this.broker.createQueue("leoz.log.queue")
    })

    private fun nodeQueue(id: Int): Queue {
        return this.broker.createQueue("leoz.node.queue." + id.toString())
    }

    private val nodeNotificationTopic: Topic by lazy({
        this.broker.createTopic("leoz.node.notification.topic")
    })

    /**
     *
     */
    override fun centralQueueChannel(): Channel {
        return Channel(connectionFactory = this.broker.connectionFactory,
                destination = this.centralQueue,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    /**
     *
     */
    fun nodeQueueChannel(id: Int): Channel {
        return Channel(connectionFactory = this.broker.connectionFactory,
                destination = this.nodeQueue(id),
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    /**
     *
     */
    fun nodeNotificationChannel(): Channel {
        return Channel(connectionFactory = this.broker.connectionFactory,
                destination = this.nodeNotificationTopic,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    /**
     * Central log channel
     */
    override fun centralLogChannel(): Channel {
        val c = Channel(
                connectionFactory = this.broker.connectionFactory,
                destination = this.centralLogQueue,
                deliveryMode = Channel.DeliveryMode.Persistent,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))

        c.priority = 1
        return c
    }

    /**
     * Central entity sync channel
     */
    override fun centralEntitySyncChannel(): Channel {
        return Channel(
                connectionFactory = this.broker.connectionFactory,
                destination = this.centralEntitySyncQueue,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }

    /**
     * Entity sync broadcast channel
     */
    override fun nodeEntitySyncBroadcastChannel(): Channel {
        return Channel(
                connectionFactory = this.broker.connectionFactory,
                destination = this.nodeEntitySyncTopic,
                converter = DefaultConverter(
                        DefaultConverter.SerializationType.KRYO,
                        DefaultConverter.CompressionType.GZIP))
    }
}
