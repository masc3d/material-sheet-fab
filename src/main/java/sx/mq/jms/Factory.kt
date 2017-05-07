package sx.mq.jms

import sx.mq.Broker
import javax.jms.ConnectionFactory
import javax.jms.Queue
import javax.jms.Topic

/**
 * JMS factory
 * Created by masc on 15/08/16.
 */
interface Factory {
    fun createConnectionFactory(): ConnectionFactory
    fun createQueue(name: String): Queue
    fun createTopic(name: String): Topic
    fun createBroker(): Broker
}