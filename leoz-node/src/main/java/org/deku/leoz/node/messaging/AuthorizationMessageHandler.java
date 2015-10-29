package org.deku.leoz.node.messaging;

import org.deku.leoz.node.messaging.entities.AuthorizationMessage;
import org.jetbrains.annotations.NotNull;
import sx.jms.Converter;
import sx.jms.Handler;

import javax.jms.Message;
import javax.jms.Session;

/**
 * Created by masc on 30.06.15.
 */
public class AuthorizationMessageHandler implements Handler<AuthorizationMessage> {
    @Override
    public void onMessage(AuthorizationMessage message, @NotNull Converter converter, @NotNull Message jmsMessage, @NotNull Session session) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
