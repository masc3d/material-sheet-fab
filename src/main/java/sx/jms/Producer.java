package sx.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Disposable;

import javax.jms.*;

/**
 * Lightweight jms producer abstraction.
 * Created by masc on 16.04.15.
 */
public abstract class Producer implements Disposable {
    Log mLog = LogFactory.getLog(this.getClass());
    ConnectionFactory mConnectionFactory;
    Connection mConnection;
    Session mSession;
    Destination mDestination;
    MessageProducer mProducer;

    public Producer(ConnectionFactory connectionFactory) {
        mConnectionFactory = connectionFactory;
    }

    public Producer(Session session) {
        mSession = session;
    }

    protected Session createSession() throws JMSException {
        throw new UnsupportedOperationException();
    }

    protected abstract Destination createDestination() throws JMSException;

    protected Session getSession() throws JMSException {
        if (mSession == null)
            mSession = this.createSession();

        return mSession;
    }

    protected Connection getConnection() throws JMSException {
        if (mConnection == null)
            mConnection = mConnectionFactory.createConnection();

        return mConnection;
    }

    protected Destination getDestination() throws JMSException {
        if (mDestination == null)
            mDestination = this.createDestination();

        return mDestination;
    }

    protected MessageProducer getProducer() throws JMSException {
        if (mProducer == null)
            mProducer = mSession.createProducer(this.getDestination());

        return mProducer;
    }

    @Override
    public void dispose() {
        if (mConnection != null)
            try {
                mConnection.stop();
            } catch (JMSException e) {
                mLog.error(e.getMessage(), e);
            }
    }
}
