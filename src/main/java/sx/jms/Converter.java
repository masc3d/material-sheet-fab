package sx.jms;

import org.springframework.jms.support.converter.MessageConversionException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * JMS message converter
 * Created by masc on 28.06.15.
 */
public interface Converter {
    /**
     * Convert a Java object to a JMS Message using the supplied session
     * to create the message object.
     * @param object  the object to convert
     * @param session the Session to use for creating a JMS Message
     * @return the JMS Message
     * @throws javax.jms.JMSException     if thrown by JMS API methods
     */
    Message toMessage(Object object, Session session) throws JMSException;

    /**
     * Convert from a JMS Message to a Java object.
     * @param message the message to convert
     * @return the converted Java object
     * @throws javax.jms.JMSException     if thrown by JMS API methods
     */
    Object fromMessage(Message message) throws JMSException;
}
