package org.deku.leoz.central.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.central.data.repositories.NodeRepository
import org.deku.leoz.node.messaging.entities.AuthorizationMessage
import org.deku.leoz.node.messaging.entities.AuthorizationRequestMessage
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener
import sx.jms.Channel
import sx.jms.Handler
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 01.07.15.
 */
@Named
class AuthorizationService
:
        Handler<AuthorizationRequestMessage> {
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var nodeRepository: NodeRepository

    interface Listener : EventListener {
        fun onAuthorized(nodeIdentityKey: String)
    }

    private val dispatcher = EventDispatcher.createThreadSafe<Listener>()
    public val delegate: EventDelegate<Listener> = dispatcher

    override fun onMessage(message: AuthorizationRequestMessage, replyChannel: Channel?) {
        try {
            log.info(message)

            // Response message
            val am = AuthorizationMessage()
            am.key = message.key

            var record = nodeRepository.findByKey(message.key)
            if (record == null) {
                val identityKey = Identity.Key(message.key)
                val conflictingRecord = nodeRepository.findByKeyStartingWith(identityKey.short)
                if (conflictingRecord != null) {
                    // Short key conflict, reject
                    am.rejected = true
                    log.warn("Node [${message.key}] has short key conflicting with [${conflictingRecord.key}] and will be rejected")
                } else {
                    // Store new node record
                    record = nodeRepository.createNew()
                    record.key = message.key
                    record.bundle = message.name
                    record.sysInfo = message.systemInfo
                    record.store()
                }
            }

            if (record != null) {
                val isAuthorized = record.authorized != null && record.authorized !== 0

                if (isAuthorized)
                    this.dispatcher.emit { it.onAuthorized(message.key) }

                if (replyChannel != null) {
                    am.authorized = isAuthorized
                    replyChannel.send(am)
                    log.info("Sent authorization [%s]".format(am))
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
