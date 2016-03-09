package org.deku.leoz.node.messaging.handlers

import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import sx.jms.Channel
import sx.jms.Handler

/**
 * Created by masc on 30.06.15.
 */
class AuthorizationHandler : Handler<AuthorizationMessage> {
    override fun onMessage(message: AuthorizationMessage, replyChannel: Channel?) {
        // TODO: Push authorization update handling. May revoke the node's authorization key
    }
}
