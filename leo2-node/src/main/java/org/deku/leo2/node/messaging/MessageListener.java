package org.deku.leo2.node.messaging;

import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.auth.Identity;
import sx.jms.listeners.SpringJmsListener;
import sx.jms.converters.DefaultConverter;

import javax.jms.Destination;

/**
 * Created by masc on 27.06.15.
 */
public class MessageListener extends SpringJmsListener {
    /** Application identity */
    Identity mIdentity;
    MessagingContext mMessagingContext;

    /**
     * c'tor
     * @param messagingContext
     */
    public MessageListener(MessagingContext messagingContext, Identity identity) {
        super(messagingContext.getBroker().getConnectionFactory());

        this.setConverter(new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP));

        mIdentity = identity;
        mMessagingContext = messagingContext;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.getNodeQueue(mIdentity.getId());
    }
}
