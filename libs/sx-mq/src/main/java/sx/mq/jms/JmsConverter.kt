package sx.mq.jms

import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * JMS message converter
 * Created by masc on 28.06.15.
 */
interface JmsConverter {
    /**
     * Convert a Java object to a JMS Message using the supplied session
     * to create the message object.
     * @param obj Object to convert
     * @param session Session to use for creating a JMS Message
     * @param onSize Optional callback passing byte size of the serialized message for statistics
     * @return the JMS Message
     * @throws javax.jms.JMSException     if thrown by JMS API methods
     */
    @Throws(JMSException::class)
    fun toMessage(obj: Any, session: Session, onSize: ((size: Long) -> Unit)? = null): Message

    /**
     * Convert from a JMS Message to a Java object.
     * @param message the message to convert
     * @param onSize Optional callback passing byte size of the serialized message for statistics
     * @return the converted Java object
     * @throws javax.jms.JMSException     if thrown by JMS API methods
     */
    @Throws(JMSException::class)
    fun fromMessage(message: Message, onSize: ((size: Long) -> Unit)? = null): Any
}
