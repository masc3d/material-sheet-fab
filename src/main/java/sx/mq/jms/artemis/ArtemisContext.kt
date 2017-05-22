package sx.mq.jms.artemis

import org.apache.activemq.artemis.jms.client.ActiveMQQueue
import org.apache.activemq.artemis.jms.client.ActiveMQTopic
import sx.mq.jms.JmsContext
import javax.jms.ConnectionFactory
import javax.jms.Queue
import javax.jms.Topic

/**
 * Created by masc on 08.05.17.
 */
class ArtemisContext(
        override val connectionFactory: ConnectionFactory) : JmsContext {
    override fun createQueue(name: String): Queue {
        return ActiveMQQueue(name)
    }

    override fun createTopic(name: String): Topic {
        return ActiveMQTopic(name)
    }
}