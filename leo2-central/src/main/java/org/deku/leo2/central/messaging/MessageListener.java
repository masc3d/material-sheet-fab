package org.deku.leo2.central.messaging;

import org.deku.leo2.messaging.MessagingContext;
import sx.jms.listeners.SpringJmsListener;
import sx.jms.converters.DefaultConverter;

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

        this.setConverter(new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP));

        mMessagingContext = messagingContext;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.getCentralQueue();
    }
}
