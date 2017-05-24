package org.deku.leoz.config

import org.apache.activemq.broker.region.virtual.CompositeTopic
import org.deku.leoz.identity.Identity
import sx.mq.MqBroker
import sx.mq.jms.JmsChannel
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQContext
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory
import sx.mq.jms.toJms

object JmsChannels {
    // JMS channels

    /** Local JMS broker context */
    val context = ActiveMQContext(connectionFactory = ActiveMQConfiguration.connectionFactory)

    object central {

        val mainQueue: JmsChannel by lazy {
            MqConfiguration.central.mainQueue.toJms(
                    context = context)
        }

        val logQueue: JmsChannel by lazy {
            MqConfiguration.central.logQueue.toJms(
                    context = context,
                    priority = 1
            )
        }

        val entitySyncQueue: JmsChannel by lazy {
            MqConfiguration.central.entitySyncQueue.toJms(
                    context = context
            )
        }

        val entitySyncTopic: JmsChannel by lazy {
            MqConfiguration.central.entitySyncTopic.toJms(
                    context = context
            )
        }
    }

    object node {
        fun queue(identityKey: Identity.Key): JmsChannel {
            return MqConfiguration
                    .node.queue(identityKey)
                    .toJms(context = context)
        }

        val topic: JmsChannel by lazy {
            MqConfiguration.node.topic.toJms(
                    context = context
            )
        }
    }

    object mobile {
        val topic by lazy {
            MqConfiguration.mobile.topic.toJms(
                    context = context
            )
        }
    }
}

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
            d.name = MqConfiguration.central.mainQueueMqtt.destinationName
            d.forwardTo = listOf(
                    JmsChannels.central.mainQueue.destination)
            d
        }())

        broker.addCompositeDestination({
            val d = CompositeTopic()
            d.name = MqConfiguration.central.logQueueMqtt.destinationName
            d.forwardTo = listOf(
                    JmsChannels.central.logQueue.destination)
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

}