package sx.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Disposable;

import javax.jms.*;
import java.util.HashMap;

/**
 * Lightweight jms listener abstraction.
 * This is the top level abstract class, only binding a connection factory.
 * Created by masc on 16.04.15.
 */
public abstract class Listener implements Disposable, ExceptionListener {
    private Log mLog;
    /** Connection factory */
    private ConnectionFactory mConnectionFactory;
    /** Object message handler delegates */
    private HashMap<Class, Handler> mHandlerDelegates = new HashMap<Class, Handler>();
    /** Message converter */
    private MessageConverter mMessageConverter = null;

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

    public MessageConverter getMessageConverter() {
        return mMessageConverter;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        mMessageConverter = messageConverter;
    }

    /**
     * Default message handler with support for object message delegates
     * @param message
     * @param session
     * @throws JMSException
     */
    protected void onMessage(Message message, Session session) throws JMSException {
        Object messageObject = null;

        Handler handler = null;

        if (mMessageConverter != null) {
            messageObject = mMessageConverter.fromMessage(message);
            handler = mHandlerDelegates.getOrDefault(messageObject.getClass(), null);
        }
        else {
            handler = mHandlerDelegates.getOrDefault(Message.class, null);
        }

        if (handler == null) {
            if (messageObject != null)
                throw new HandlingException(String.format("No delegate for message object type [%s]",
                        messageObject.getClass(),
                        Message.class));

            throw new HandlingException("No message converter nor generic delegate for jms messages");
        }

        handler.onMessage(messageObject, message, session);
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
