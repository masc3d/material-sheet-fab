package org.deku.leoz.config

import org.apache.activemq.command.ActiveMQQueue
import org.apache.activemq.command.ActiveMQTopic
import org.deku.leoz.identity.Identity
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.jms.Channel
import sx.jms.converters.DefaultConverter
import sx.jms.Broker
import sx.jms.activemq.ActiveMQBroker
import sx.jms.activemq.ActiveMQPooledConnectionFactory

/**
 * ActiveMQ specific messaging configuration
 * @param connectionFactory Optional connection factory. If not set reverts to the local broker URI.
 * Created by masc on 16.04.15.
 */
class ActiveMQConfiguration() {

    // TODO: migrate to Kodein module

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

    init {
        // Configure broker authentication
        this.broker.user = Broker.User(USERNAME, PASSWORD, GROUPNAME)
    }

    val connectionFactory: ActiveMQPooledConnectionFactory by lazy {
        ActiveMQPooledConnectionFactory(
                ActiveMQBroker.Companion.instance.localUri,
                USERNAME,
                PASSWORD)
    }

    val broker: ActiveMQBroker
        get() = ActiveMQBroker.instance

    val centralQueue: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQQueue("leoz.central.queue"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    val centralLogQueue: Channel.Configuration by lazy {
        val c = Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQQueue("leoz.log.queue"),
                deliveryMode = Channel.DeliveryMode.Persistent,
                converter = DefaultConverter(KryoSerializer().gzip))

        c.priority = 1
        c
    }

    val entitySyncQueue: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQQueue("leoz.entity-sync.queue"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    val entitySyncTopic: Channel.Configuration by lazy {
        Channel.Configuration(
                connectionFactory = this.connectionFactory,
                destination = ActiveMQTopic("leoz.entity-sync.topic"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    fun nodeQueue(identityKey: Identity.Key): Channel.Configuration {
        return Channel.Configuration(connectionFactory = this.connectionFactory,
                destination = ActiveMQQueue("leoz.node.queue." + identityKey.short),
                converter = DefaultConverter(KryoSerializer().gzip))
    }

    val nodeTopic: Channel.Configuration by lazy {
        Channel.Configuration(connectionFactory = this.connectionFactory,
                destination = ActiveMQTopic("leoz.node.topic"),
                converter = DefaultConverter(KryoSerializer().gzip))
    }
}
