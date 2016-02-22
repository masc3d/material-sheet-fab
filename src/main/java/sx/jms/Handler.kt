package sx.jms

import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Message handler
 * Created by masc on 28.06.15.
 */
interface Handler<in T> {
    /** Override for serialized/object message handling */
    fun onMessage(message: T, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory)
}
