package org.deku.leoz.config

import org.apache.activemq.broker.region.virtual.CompositeTopic
import org.deku.leoz.identity.Identity
import sx.mq.MqBroker
import sx.mq.jms.JmsChannel
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory
import sx.mq.jms.toJms
import sx.time.Duration

/**
 * ActiveMQ specific messaging configuration
 * Created by masc on 16.04.15.
 */
object ActiveMQConfiguration {

    /** Local JMS broker */
    val broker: ActiveMQBroker by lazy {
        val broker = ActiveMQBroker.instance

        // Configure broker authentication
        broker.user = MqBroker.User(
                MqConfiguration.USERNAME,
                MqConfiguration.PASSWORD,
                MqConfiguration.GROUPNAME)

        // Setup composite destinations for MQTT
        // REMARK: MQTT doesn't support queues natively, thus using topics as virtual endpoints
        // which are forwarded to queues internally.
        broker.addCompositeDestination({
            val d = CompositeTopic()
            d.name = MqConfiguration.centralQueueTopic.destinationName
            d.forwardTo = listOf(
                    this.centralQueue.destination)
            d
        }())

        broker.addCompositeDestination({
            val d = CompositeTopic()
            d.name = MqConfiguration.centralLogQueueTopic.destinationName
            d.forwardTo = listOf(
                    this.centralLogQueue.destination)
            d
        }())

        broker
    }

    /**
     * Local JMS broker connection factory
     */
    val connectionFactory: ActiveMQPooledConnectionFactory by lazy {
        ActiveMQPooledConnectionFactory(
                ActiveMQBroker.instance.localUri,
                MqConfiguration.USERNAME,
                MqConfiguration.PASSWORD)
    }

    /** Local JMS broker context */
    val context = ActiveMQContext(connectionFactory = this.connectionFactory)

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

    val mobileTopic: JmsChannel by lazy {
        MqConfiguration.mobileTopic.toJms(
                ttl = Duration.ofDays(1),
                context = this.context
        )
    }
}
