package sx.jms

import javax.jms.JMSException
import javax.jms.Message
import javax.jms.Session

/**
 * JMS message converter
 * Created by masc on 28.06.15.
 */
interface Converter {
    /**
     * Convert a Java object to a JMS Message using the supplied session
     * to create the message object.
     * @param obj  the object to convert
     * @param session the Session to use for creating a JMS Message
     * @return the JMS Message
     * @throws javax.jms.JMSException     if thrown by JMS API methods
     */
    @Throws(JMSException::class)
    fun toMessage(obj: Any, session: Session): Message

    /**
     * Convert from a JMS Message to a Java object.
     * @param message the message to convert
     * @return the converted Java object
     * @throws javax.jms.JMSException     if thrown by JMS API methods
     */
    @Throws(JMSException::class)
    fun fromMessage(message: Message): Any
}
