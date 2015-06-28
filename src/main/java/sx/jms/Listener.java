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

    /**
     * Add handler delegate for handling messages of specific (object) type
     * Delegate handlers requires a converter to be set.
     * @param c
     * @param delegate
     * @param <T>
     */
    public <T> void addDelegate(Class<T> c, Handler<T> delegate) {
        synchronized (mHandlerDelegates) {
            mHandlerDelegates.put(c, delegate);
        }
    }

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch(Exception e) {
            mLog.error(e.getMessage(), e);
        }
    }

    @Override
    public void onException(JMSException e) {
        mLog.error(e.getMessage(), e);
    }
}
