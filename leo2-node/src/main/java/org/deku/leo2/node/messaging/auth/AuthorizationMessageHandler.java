package org.deku.leo2.node.messaging.auth;

import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import sx.jms.Handler;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Created by masc on 30.06.15.
 */
public class AuthorizationMessageHandler implements Handler<AuthorizationMessage> {
    @Override
    public void onMessage(AuthorizationMessage message, Message jmsMessage, Session session) throws JMSException {
        // TODO: Push authorization update handling
    }
}
