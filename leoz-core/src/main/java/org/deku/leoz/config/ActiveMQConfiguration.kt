package org.deku.leoz.config

import org.deku.leoz.identity.Identity
import sx.io.serialization.KryoSerializer
import sx.io.serialization.gzip
import sx.mq.Broker
import sx.mq.jms.JmsChannel
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory
import sx.mq.jms.converters.DefaultJmsConverter
import sx.mq.jms.toJms

/**
 * ActiveMQ specific messaging configuration
 * Created by masc on 16.04.15.
 */
object ActiveMQConfiguration {

    init {
        // Configure broker authentication
        this.broker.user = Broker.User(
                MqConfiguration.USERNAME,
                MqConfiguration.PASSWORD,
                MqConfiguration.GROUPNAME)
    }

    /**
     * Local JMS broker connection factory
     */
    val connectionFactory: ActiveMQPooledConnectionFactory by lazy {
        ActiveMQPooledConnectionFactory(
                ActiveMQBroker.Companion.instance.localUri,
                MqConfiguration.USERNAME,
                MqConfiguration.PASSWORD)
    }

    /** Local JMS broker context */
    val context = ActiveMQContext(connectionFactory = this.connectionFactory)

    /** Local JMS broker */
    val broker: ActiveMQBroker get() = ActiveMQBroker.instance

    // JMS channels

    val centralQueue: JmsChannel by lazy {
        MqConfiguration.centralQueue.toJms(
                context = this.context)
    }

    val centralLogQueue: JmsChannel by lazy {
        MqConfiguration.centralLogQueue.toJms(
                context = this.context,
                priority = 1
        )
    }

    val entitySyncQueue: JmsChannel by lazy {
        MqConfiguration.entitySyncQueue.toJms(
                context = this.context
        )
    }

    val entitySyncTopic: JmsChannel by lazy {
        MqConfiguration.entitySyncTopic.toJms(
                context = this.context
        )
    }

    fun nodeQueue(identityKey: Identity.Key): JmsChannel {
        return MqConfiguration
                .nodeQueue(identityKey)
                .toJms(context = this.context)
    }

    val nodeTopic: JmsChannel by lazy {
        MqConfiguration.nodeTopic.toJms(
                context = this.context
        )
    }
}
