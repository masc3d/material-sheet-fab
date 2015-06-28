package sx.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Disposable;

import javax.jms.*;

/**
 * Lightweight jms listener abstraction.
 * This is the top level abstract class, only binding a connection factory.
 * Created by masc on 16.04.15.
 */
public abstract class Listener implements Disposable, ExceptionListener, Handler<Message> {
    private Log mLog;
    private ConnectionFactory mConnectionFactory;

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
