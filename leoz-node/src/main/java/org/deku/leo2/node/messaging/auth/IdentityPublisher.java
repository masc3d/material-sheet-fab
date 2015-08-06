package org.deku.leo2.node.messaging.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.auth.Identity;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import org.deku.leo2.node.messaging.auth.v1.IdentityMessage;
import sx.jms.converters.DefaultConverter;

import javax.jms.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by masc on 01.07.15.
 */
public class IdentityPublisher {
    private Log mLog = LogFactory.getLog(this.getClass());

    private MessagingContext mMessagingContext;
    DefaultConverter mConverter = new DefaultConverter(
            DefaultConverter.SerializationType.KRYO,
            DefaultConverter.CompressionType.GZIP);

    /**
     * c'tor
     * @param messagingContext
     */
    public IdentityPublisher(MessagingContext messagingContext) {
        mMessagingContext = messagingContext;
    }

    /**
     * Publishes client node idenity to the central system
     * @param identity
     */
    public void publish(Identity identity) throws TimeoutException, Exception {
        this.sendAndReceive(identity, false);
    }

    /**
     * Request client node id from central system
     * @param identity
     */
    public AuthorizationMessage requestId(Identity identity) throws TimeoutException, Exception {
        return this.sendAndReceive(identity, true);
    }

    /**
     *
     * @param identity
     * @param receive
     */
    @SuppressWarnings("unchecked")
    private AuthorizationMessage sendAndReceive(
            Identity identity,
            boolean receive) throws TimeoutException, Exception {

        // Connection and session
        Connection cn = mMessagingContext.getBroker().getConnectionFactory().createConnection();
        cn.start();
        Session session = cn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue receiveQueue = null;

        // Message producer
        MessageProducer mp = session.createProducer(mMessagingContext.getCentralQueue());
        mp.setTimeToLive(TimeUnit.SECONDS.toMillis(10));
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
