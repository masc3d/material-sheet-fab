package org.deku.leoz.central.messaging;

import org.deku.leoz.config.MessagingConfiguration;
import sx.jms.listeners.SpringJmsListener;
import sx.jms.converters.DefaultConverter;

import javax.jms.Destination;

/**
 * Created by masc on 27.06.15.
 */
public class MessageListener extends SpringJmsListener {
    /** Application identity */
    MessagingConfiguration mMessagingConfiguration;

    /**
     * c'tor
     * @param messagingConfiguration
     */
    public MessageListener(MessagingConfiguration messagingConfiguration) {
        super(messagingConfiguration.getBroker().getConnectionFactory());

        this.setConverter(new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP));

        mMessagingConfiguration = messagingConfiguration;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingConfiguration.getCentralQueue();
    }
}
