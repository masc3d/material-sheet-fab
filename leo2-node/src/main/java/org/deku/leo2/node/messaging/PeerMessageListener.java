package org.deku.leo2.node.messaging;

import org.deku.leo2.messaging.MessagingContext;
import org.deku.leo2.node.auth.Identity;
import sx.jms.SpringJmsListener;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Created by masc on 27.06.15.
 */
public class PeerMessageListener extends SpringJmsListener {
    /** Application identity */
    Integer mPeerId;
    MessagingContext mMessagingContext;

    public static String getQueueName(Integer peerId) {
        return "leo2.node." + peerId.toString();
    }

    /**
     * c'tor
     * @param messagingContext
     */
    public PeerMessageListener(MessagingContext messagingContext, Integer peerId) {
        super(messagingContext.getConnectionFactory());
        mMessagingContext = messagingContext;
    }

    @Override
    protected Destination createDestination() {
        return mMessagingContext.createQueue(getQueueName(mPeerId));
    }

    @Override
    public void onMessage(Message message, Session session) {

    }
}
