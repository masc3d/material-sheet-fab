package sx.jms;

import sx.Disposable;

import javax.jms.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lightweight jms listener abstraction.
 * This is the top level abstract class, only binding a connection factory.
 * Created by masc on 16.04.15.
 */
public abstract class Listener implements Disposable, MessageListener, ExceptionListener {
    private Logger mLogger;
    private ConnectionFactory mConnectionFactory;

    public Listener(ConnectionFactory connectionFactory) {
        mLogger = Logger.getLogger(this.getClass().getName());
        mConnectionFactory = connectionFactory;
    }

    public abstract void start() throws JMSException;

    public abstract void stop() throws JMSException;

    protected Logger getLogger() {
        return mLogger;
    }
    protected ConnectionFactory getConnectionFactory() {
        return mConnectionFactory;
    }

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch(Exception e) {
            mLogger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void onException(JMSException e) {
        mLogger.log(Level.SEVERE, e.getMessage(), e);
    }
}
