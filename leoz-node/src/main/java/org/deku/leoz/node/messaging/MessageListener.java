package org.deku.leoz.node.messaging;

import org.deku.leoz.config.MessagingConfiguration;
import org.deku.leoz.node.auth.Identity;
import sx.jms.converters.DefaultConverter;
import sx.jms.listeners.SpringJmsListener;

import javax.jms.Destination;

/**
 * Global message listener.
 * Handlers are attached during runtime.s
 * Created by masc on 27.06.15.
 */
public class MessageListener extends SpringJmsListener {
    /** Application identity */
    Identity mIdentity;
    MessagingConfiguration mMessagingConfiguration;

    /**
     * c'tor
     * @param messagingConfiguration
     */
    public MessageListener(MessagingConfiguration messagingConfiguration, Identity identity) {
        super(messagingConfiguration.getBroker().getConnectionFactory());

        this.setConverter(new DefaultConverter(
                DefaultConverter.SerializationType.KRYO,
                DefaultConverter.CompressionType.GZIP));

        mIdentity = identity;
        mMessagingConfiguration = messagingConfiguration;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingConfiguration.nodeQueue(mIdentity.getId());
    }
}
