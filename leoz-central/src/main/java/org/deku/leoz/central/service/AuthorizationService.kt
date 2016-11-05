package org.deku.leoz.central.service

import org.deku.leoz.Identity
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.node.service.authorization.AuthorizationMessage
import org.deku.leoz.node.service.authorization.AuthorizationRequestMessage
import org.slf4j.LoggerFactory
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener
import sx.jms.Channel
import sx.jms.Handler
import sx.logging.slf4j.info
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by masc on 01.07.15.
 */
@Named
class AuthorizationService
:
        Handler<AuthorizationRequestMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    interface Listener : EventListener {
        fun onAuthorized(nodeIdentityKey: Identity.Key)
    }

    private val dispatcher = EventDispatcher.createThreadSafe<Listener>()
    public val delegate: EventDelegate<Listener> = dispatcher

    override fun onMessage(message: AuthorizationRequestMessage, replyChannel: Channel?) {
        try {
            log.info(message)

            // Response message
            val am = AuthorizationMessage()
            am.key = message.key

            val identityKey = Identity.Key(message.key)
            var record = nodeJooqRepository.findByKey(message.key)
            if (record == null) {
                val conflictingRecord = nodeJooqRepository.findByKeyStartingWith(identityKey.short)
                if (conflictingRecord != null) {
                    // Short key conflict, reject
                    am.rejected = true
                    log.warn("Node [${message.key}] has short key conflicting with [${conflictingRecord.key}] and will be rejected")
                } else {
                    // Store new node record
                    record = nodeJooqRepository.createNew()
                    record.key = message.key
                    record.bundle = message.name
                    record.sysInfo = message.systemInfo
                    record.store()
                }
            }

            if (record != null) {
                val isAuthorized = record.authorized != null && record.authorized !== 0

                if (isAuthorized)
                    this.dispatcher.emit { it.onAuthorized(identityKey) }

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
