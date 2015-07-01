package org.deku.leo2.node.messaging.auth;

import org.deku.leo2.node.auth.IdentityConfiguration;
import org.deku.leo2.node.messaging.auth.v1.AuthorizationMessage;
import sx.jms.Handler;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by masc on 30.06.15.
 */
public class AuthorizationHandler implements Handler<AuthorizationMessage> {
    @Override
    public void onMessage(AuthorizationMessage message, Session session) throws JMSException {
        IdentityConfiguration.instance().getIdentity().setId(message.getId());
    }
}
