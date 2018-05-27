package sx.mq.jms

import sx.mq.DestinationType
import sx.mq.MqEndpoint
import javax.jms.ConnectionFactory
import javax.jms.Destination
import javax.jms.Queue
import javax.jms.Topic

/**
 * JMS context, providing JMS specific resources or means to create those as needed
 * Created by masc on 15/08/16.
 */
interface JmsContext {
    val connectionFactory: ConnectionFactory
    fun createQueue(name: String): Queue
    fun createTopic(name: String): Topic

    fun createDestination(channel: MqEndpoint): Destination {
        return when (channel.destinationType) {
            DestinationType.Queue -> this.createQueue(channel.destinationName)
            DestinationType.Topic -> this.createTopic(channel.destinationName)
        }
    }
}