package sx.jms;

import javax.jms.*;
import java.util.List;

/**
 * Simple single threaded jms listener
 * Created by masc on 17.04.15.
 */
public abstract class SimpleListener extends Listener {
    Connection mConnection;
    Session mSession;

    public SimpleListener(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    protected Connection createConnection() throws JMSException {
        return this.getConnectionFactory().createConnection();
    }

    protected abstract Session createSession(Connection connection) throws JMSException;

    protected abstract List<Destination> createDestinations(Session session) throws JMSException;

    protected Connection getConnection() {
        return mConnection;
    }

    protected Session getSession() {
        return mSession;
    }

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
                    SimpleListener.this.onMessage(message);
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

    @Override
    public void stop() throws JMSException {
        if (this.mConnection != null)
            mConnection.close();
    }
}
