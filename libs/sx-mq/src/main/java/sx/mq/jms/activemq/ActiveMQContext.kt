package sx.mq.jms.activemq

import org.apache.activemq.command.ActiveMQQueue
import org.apache.activemq.command.ActiveMQTopic
import sx.mq.jms.JmsContext
import javax.jms.ConnectionFactory
import javax.jms.Queue
import javax.jms.Topic

/**
 * ActiveMQ specific factory
 * Created by masc on 08.05.17.
 */
class ActiveMQContext(
        override val connectionFactory: ConnectionFactory
) : JmsContext {
    override fun createQueue(name: String): Queue {
        return ActiveMQQueue(name)
    }

    override fun createTopic(name: String): Topic {
        return ActiveMQTopic(name)
    }
}