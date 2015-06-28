package sx.jms;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Message handler
 * Created by masc on 28.06.15.
 */
public interface Handler<T> {
    void onMessage(T message, Session session) throws JMSException;
}
