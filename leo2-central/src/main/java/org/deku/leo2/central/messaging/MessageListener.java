package org.deku.leo2.central.messaging;

import org.deku.leo2.messaging.MessagingContext;
import sx.jms.SpringJmsListener;
import sx.jms.converters.DefaultMessageConverter;

import javax.jms.Destination;

/**
 * Created by masc on 27.06.15.
 */
public class MessageListener extends SpringJmsListener {
    /** Application identity */
    MessagingContext mMessagingContext;

    /**
     * c'tor
     * @param messagingContext
     */
    public MessageListener(MessagingContext messagingContext) {
        super(messagingContext.getBroker().getConnectionFactory());

        this.setMessageConverter(new DefaultMessageConverter(
                DefaultMessageConverter.SerializationType.KRYO,
                DefaultMessageConverter.CompressionType.GZIP));

        mMessagingContext = messagingContext;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.getCentralQueue();
    }
}
