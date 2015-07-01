package sx.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Disposable;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight jms listener abstraction.
 * This is the top level abstract class, only binding a connection factory.
 * Created by masc on 16.04.15.
 */
public abstract class Listener implements Disposable, ExceptionListener, Handler<Message> {
    private Log mLog;
    private ConnectionFactory mConnectionFactory;
    private HashMap<Class, Handler> mHandlerDelegates = new HashMap<Class, Handler>();
    private Converter mConverter = null;

    /**
     * Message handling exception
     */
    private class HandlingException extends RuntimeException {
        public HandlingException(String message) {
            super(message);
        }
    }

    public Listener(ConnectionFactory connectionFactory) {
        mLog = LogFactory.getLog(this.getClass());
        mConnectionFactory = connectionFactory;
    }

    public abstract void start() throws JMSException;

    public abstract void stop() throws JMSException;

    protected Log getLog() {
        return mLog;
    }

    protected ConnectionFactory getConnectionFactory() {
        return mConnectionFactory;
    }

    public Converter getConverter() {
        return mConverter;
    }

    public void setConverter(Converter converter) {
        mConverter = converter;
    }

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        Object messageObject = null;

        if (mConverter != null)
            messageObject = mConverter.fromMessage(message);

        Handler handler = null;

        if (messageObject != null)
            handler = mHandlerDelegates.getOrDefault(messageObject.getClass(), null);

        if (handler == null) {
            handler = mHandlerDelegates.getOrDefault(Message.class, null);
        }

        if (handler == null) {
            throw new HandlingException(String.format("No delegate for object type [%s] and no generic delegate for [%s]",
                    messageObject.getClass(),
                    Message.class));
        }

        handler.onMessage(messageObject, session);
    }

    /**
     * Add handler delegate for handling messages of specific (object) type
     * Delegate handlers requires a converter to be set.
     * @param c Class of object/message to process
     * @param delegate Handler
     * @param <T> Type of object/message to process
     */
    public <T> void addDelegate(Class<T> c, Handler<T> delegate) {
        mHandlerDelegates.put(c, delegate);
    }

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch (Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }

    @Override
    public void onException(JMSException e) {
        mLog.error(e.getMessage(), e);
    }
}
