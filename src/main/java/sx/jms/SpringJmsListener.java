package sx.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * Spring listener implementation
 * Created by masc on 07.06.15.
 */
public abstract class SpringJmsListener extends Listener implements SessionAwareMessageListener, ErrorHandler {
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
     * Can be overridden to customize listener container configuration
     * @param listenerContainer
     */
    protected void configure(DefaultMessageListenerContainer listenerContainer) { }

    @Override
    public void start() throws JMSException {
        if (mListenerContainer == null) {
            mDestination = this.createDestination();

            mListenerContainer = new DefaultMessageListenerContainer();
            mListenerContainer.setConnectionFactory(this.getConnectionFactory());
            mListenerContainer.setMessageListener(this);
            mListenerContainer.setSessionTransacted(true);
            mListenerContainer.setErrorHandler(this);
            mListenerContainer.setDestination(mDestination);
            this.configure(mListenerContainer);
            mListenerContainer.afterPropertiesSet();
        }
        mListenerContainer.start();
    }

    @Override
    public void stop() throws JMSException {
        mListenerContainer.stop();
    }

    @Override
    public void handleError(Throwable t) {
        mLog.error(t.getMessage(), t);
    }

    @Override
    public void dispose() {
        try {
            this.stop();
        } catch (JMSException e) {
        }
    }
}
