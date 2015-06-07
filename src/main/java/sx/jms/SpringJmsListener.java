package sx.jms;

import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.*;
import java.util.List;

/**
 * Spring listener implementation
 * Created by masc on 07.06.15.
 */
public abstract class SpringJmsListener extends Listener {
    private Destination mDestination;
    private DefaultMessageListenerContainer mListenerContainer;

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
    public void dispose() {
        try {
            this.stop();
        } catch (JMSException e) {
        }
    }
}
