package org.deku.leoz.node.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leoz.Identity;
import org.deku.leoz.config.messaging.MessagingConfiguration;
import org.deku.leoz.node.messaging.entities.AuthorizationMessage;
import org.deku.leoz.node.messaging.entities.IdentityMessage;
import sx.jms.converters.DefaultConverter;

import javax.jms.*;
import java.util.concurrent.TimeoutException;

/**
 * Created by masc on 01.07.15.
 */
public class IdentityPublisher {
    private Log mLog = LogFactory.getLog(this.getClass());

    private MessagingConfiguration mMessagingConfiguration;
    DefaultConverter mConverter = new DefaultConverter(
            DefaultConverter.SerializationType.KRYO,
            DefaultConverter.CompressionType.GZIP);

    /**
     * c'tor
     * @param messagingConfiguration
     */
    public IdentityPublisher(MessagingConfiguration messagingConfiguration) {
        mMessagingConfiguration = messagingConfiguration;
    }

    /**
     * Publishes client node idenity to the central system
     * @param identity
     */
    public void publish(Identity identity) throws Exception {
        this.sendAndReceive(identity, false);
    }

    /**
     * Request client node id from central system
     * @param identity
     */
    public AuthorizationMessage requestId(Identity identity) throws Exception {
        return this.sendAndReceive(identity, true);
    }

    /**
     *
     * @param identity
     * @param receive
     */
    private AuthorizationMessage sendAndReceive(
            Identity identity,
            boolean receive) throws Exception {

        // Connection and session
        Connection cn = mMessagingConfiguration.getBroker().getConnectionFactory().createConnection();
        cn.start();
        Session session = cn.createSession(true, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue receiveQueue = null;

        // Message producer
        MessageProducer mp = session.createProducer(mMessagingConfiguration.getCentralQueue());
        mp.setPriority(8);
        mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Setup message
        IdentityMessage identityMessage = new IdentityMessage();
        identityMessage.setId(identity.getId());
        identityMessage.setKey(identity.getKey());
        identityMessage.setHardwareAddress(identity.getSystemInformation().getHardwareAddress());

        // Serialize system info to json
        ObjectMapper jsonMapper = new ObjectMapper();
        String systemInformationJson;
        try {
            systemInformationJson = jsonMapper.writeValueAsString(identity.getSystemInformation());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        identityMessage.setSystemInfo(systemInformationJson);

        // Convert and send
        Message message = mConverter.toMessage(identityMessage, session);
        if (receive) {
            receiveQueue = session.createTemporaryQueue();
            message.setJMSReplyTo(receiveQueue);
        }
        mp.send(message);
        session.commit();

        // Receive authorization message (on demand)
        if (receive) {
            MessageConsumer mc = session.createConsumer(receiveQueue);
            message = mc.receive(10 * 1000);

            if (message == null)
                throw new TimeoutException("Timeout while waiting for authorization response");

            return (AuthorizationMessage)mConverter.fromMessage(message);
        } else {
            return null;
        }
    }
}
