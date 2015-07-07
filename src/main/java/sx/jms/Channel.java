package sx.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sx.Action;
import sx.Disposable;
import sx.Dispose;
import sx.LazyInstance;

import javax.jms.*;
import javax.jms.IllegalStateException;
import java.io.Closeable;
import java.util.function.Supplier;

/**
 * Lightweight messaging channel
 * Created by masc on 06.07.15.
 */
public class Channel implements Disposable, Closeable {
    private Log mLog = LogFactory.getLog(this.getClass());

    private ConnectionFactory mConnectionFactory;
    private Converter mConverter;
    private Destination mDestination;
    private LazyInstance<Connection> mConnection = new LazyInstance<>();
    private LazyInstance<Session> mSession = new LazyInstance<>();
    private LazyInstance<MessageConsumer> mConsumer = new LazyInstance<>();

    private int mJmsDeliveryMode;
    private boolean mJmsSessionTransacted;
    private long mJmsTtl;
    private Integer mJmsPriority;
    private boolean mSessionCreated = false;

    /**
     * c'tor
     * @param connectionFactory Connection factory used to create session
     * @param destination Destination for this channel
     * @param converter Message converter to use
     * @param transacted Session transacted or not
     * @param deliveryMode JMS delivery mode
     * @param ttl JMS message time to live
     * @param priority JMS message priority
     */
    public Channel(ConnectionFactory connectionFactory, Destination destination, Converter converter, boolean transacted, int deliveryMode, long ttl, Integer priority) {
        mConnectionFactory = connectionFactory;
        mDestination = destination;
        mConverter = converter;
        mJmsSessionTransacted = transacted;
        mJmsDeliveryMode = deliveryMode;
        mJmsTtl = ttl;
        mJmsPriority = priority;

        mConnection.set(() -> {
            Connection cn = null;
            try {
                cn = mConnectionFactory.createConnection();
                cn.start();
                return cn;
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        mSession.set(() -> {
            try {
                Session session = mConnection.get().createSession(transacted, deliveryMode);
                mSessionCreated = true;
                return session;
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        mConsumer.set(() -> {
            try {
                return mSession.get().createConsumer(mDestination);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public Channel(ConnectionFactory connectionFactory, Destination destination, Converter converter, boolean transacted, int deliveryMode, long ttl) {
        this(connectionFactory, destination, converter, transacted, deliveryMode, ttl, null);
    }

    /**
     * Send jms message
     * @param message Message to send
     * @param messageConfigurer Callback for customizing the message before sending
     */
    public void send(Message message, Action<Message> messageConfigurer) throws JMSException {
        MessageProducer mp = mSession.get().createProducer(mDestination);

        mp.setDeliveryMode(mJmsDeliveryMode);
        mp.setTimeToLive(mJmsTtl);
        if (mJmsPriority != null)
            mp.setPriority(mJmsPriority);

        if (messageConfigurer != null)
            messageConfigurer.perform(message);

        mp.send(mDestination, message);
    }

    /**
     * Send jms message
     * @param message Message to send
     */
    public void send(Message message) throws JMSException {
        this.send(message, null);
    }

    /**
     * Send object as message using converter
     * @param message
     */
    public void send(Object message) throws JMSException {
        if (mConverter == null)
            throw new IllegalStateException("Cannot send object without a message converter");

        this.send(mConverter.toMessage(message, mSession.get()));
    }

    public <T> T receive(Class<T> messageType) throws JMSException {
        return null;
    }

    /**
     * Explicitly commit transaction
     */
    public void commit() throws JMSException{
        Session session = mSession.get();
        if (session.getTransacted())
            session.commit();
    }

    @Override
    public void close() {
        if (mConsumer.ifSet(c -> {
            try {
                c.close();
            } catch (JMSException e) {
                mLog.error(e.getMessage(), e);
            }
        });

        try {
            this.commit();
        } catch (JMSException e) {
            mLog.error(e.getMessage(), e);
        }

        if (mSessionCreated) {
            mSession.ifSet(s -> {
                try {
                    s.close();
                } catch (JMSException e) {
                    mLog.error(e.getMessage(), e);
                }
            });
        }

        mConnection.ifSet(c -> {
            try {
                c.close();
            } catch (JMSException e) {
                mLog.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void dispose() {
        this.close();
    }
}
