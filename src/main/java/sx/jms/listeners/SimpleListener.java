package sx.jms.listeners;

import sx.jms.Listener;

import javax.jms.*;
import java.util.List;

/**
 * Simple single threaded jms listener
 * Created by masc on 17.04.15.
 */
public abstract class SimpleListener extends Listener {
    Connection mConnection;
    Session mSession;

    /**
     * c'tor
     * @param connectionFactory
     */
    public SimpleListener(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    /**
     * Optionally override to create connection
     * @return JMS connection
     * @throws JMSException
     */
    protected Connection createConnection() throws JMSException {
        return this.getConnectionFactory().createConnection();
    }

    /**
     * Override to create customized session
     * @param connection
     * @return JMS session
     * @throws JMSException
     */
    protected abstract Session createSession(Connection connection) throws JMSException;

    /**
     * Override to create destinations to listen on
     * @param session
     * @return JMS destinations
     * @throws JMSException
     */
    protected abstract List<Destination> createDestinations(Session session) throws JMSException;

    protected Connection getConnection() {
        return mConnection;
    }

    protected Session getSession() {
        return mSession;
    }

    /**
     * Start listener
     * @throws JMSException
     */
    @Override
    public void start() throws JMSException {
        this.stop();

        mConnection = this.createConnection();
        mConnection.start();

        mSession = this.createSession(mConnection);

        List<Destination> dests = this.createDestinations(mSession);
        for (Destination d : dests) {
            MessageConsumer mc = mSession.createConsumer(d);

            // Wrap message callback, adding jms transaction support
            mc.setMessageListener( (message) -> {
                boolean transacted = false;
                try {
                    transacted = mSession.getTransacted();
                    SimpleListener.this.onMessage(message, mSession);
                    if (mSession.getTransacted())
                        mSession.commit();
                } catch(Exception e) {
                    // TODO: verify if exception is routed to onException handler when not caught here
                    this.getLog().error(e.getMessage(), e);

                    if (transacted) {
                        try {
                            mSession.rollback();
                        } catch (JMSException e1) {
                            this.getLog().error(e.getMessage(), e);
                        }
                    }
                }
            });
        }
    }

    /**
     * Stop listener
     * @throws JMSException
     */
    @Override
    public void stop() throws JMSException {
        if (this.mConnection != null)
            mConnection.close();
    }
}
