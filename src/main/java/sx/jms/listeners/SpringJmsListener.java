package sx.jms.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.util.ErrorHandler;
import sx.jms.Listener;

import javax.jms.*;

/**
 * Spring listener implementation
 * Created by masc on 07.06.15.
 */
public abstract class SpringJmsListener extends Listener implements ErrorHandler {
    private Log mLog = LogFactory.getLog(this.getClass());

    /** Destination this listener is attached to */
    private Destination mDestination;
    /** Spring message listener container */
    private DefaultMessageListenerContainer mListenerContainer;
    /** Transaction manager */
    private JmsTransactionManager mTransactionManager;

    /**
     * c'tor
     * @param connectionFactory
     */
    public SpringJmsListener(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    /**
     * Creates listener destination
     * @return
     */
    protected abstract Destination createDestination();

    /**
     * Optionally overridden to customize listener container configuration
     * @param listenerContainer
     */
    protected void configure(DefaultMessageListenerContainer listenerContainer) { }

    /**
     * Start listener
     */
    @Override
    public void start() {
        mLog.info(String.format("Starting %s", this.getClass().getSimpleName()));
        if (mListenerContainer == null) {
            mDestination = this.createDestination();

            mListenerContainer = new DefaultMessageListenerContainer();
            mListenerContainer.setConnectionFactory(this.getConnectionFactory());
            mListenerContainer.setMessageListener(new SessionAwareMessageListener() {
                @Override
                public void onMessage(Message message, Session session) throws JMSException {
                    SpringJmsListener.this.onMessage(message, session);
                }
            });
            mListenerContainer.setSessionTransacted(true);
            mListenerContainer.setErrorHandler(this);
            mListenerContainer.setDestination(mDestination);
            this.configure(mListenerContainer);
            mListenerContainer.afterPropertiesSet();
        }
        mListenerContainer.start();
    }

    /**
     * Stop listener
     */
    @Override
    public void stop() {
        mLog.info(String.format("Stopping %s", this.getClass().getSimpleName()));
        mListenerContainer.shutdown();
    }

    @Override
    public void handleError(Throwable t) {
        mLog.error(t.getMessage(), t);
    }

    @Override
    public void dispose() {
        this.stop();
    }
}
