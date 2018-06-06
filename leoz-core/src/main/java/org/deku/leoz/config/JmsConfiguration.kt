package org.deku.leoz.config

import org.apache.activemq.broker.region.virtual.CompositeQueue
import org.apache.activemq.broker.region.virtual.CompositeTopic
import sx.mq.MqBroker
import sx.mq.jms.activemq.ActiveMQBroker
import sx.mq.jms.activemq.ActiveMQPooledConnectionFactory

/**
 * ActiveMQ specific messaging configuration
 * Created by masc on 16.04.15.
 */
object JmsConfiguration {

    /** Local JMS broker */
    val broker: ActiveMQBroker by lazy {
        val broker = ActiveMQBroker.instance

        // Configure broker authentication
        broker.user = MqBroker.User(
                MqConfiguration.USERNAME,
                MqConfiguration.PASSWORD,
                MqConfiguration.GROUPNAME)

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