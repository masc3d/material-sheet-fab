package org.deku.leoz.node.messaging

import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import sx.jms.Converter
import sx.jms.Handler

import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Created by masc on 30.06.15.
 */
class AuthorizationMessageHandler : Handler<AuthorizationMessage> {
    override fun onMessage(message: AuthorizationMessage, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
