package org.deku.leoz.central.service.internal

import org.deku.leoz.Identity
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.mobile.MobileIdentityFactory
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.AuthorizationService
import org.slf4j.LoggerFactory
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener
import sx.jms.Handler
import sx.logging.slf4j.info
import sx.rs.auth.ApiKey
import sx.security.DigestType
import sx.security.getInstance
import sx.text.parseHex
import sx.text.toHexString
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Authorization service
 * Created by masc on 01.07.15.
 */
@Named
@ApiKey(false)
@Path("internal/v1/authorize")
class AuthorizationService
:
        org.deku.leoz.service.internal.AuthorizationService,
        Handler<AuthorizationService.NodeRequest> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Inject
    private lateinit var userRepository: UserJooqRepository

    interface Listener : EventListener {
        fun onAuthorized(nodeIdentityKey: Identity.Key)
    }

    // TODO: replace with rx event
    private val dispatcher = EventDispatcher.createThreadSafe<Listener>()
    public val delegate: EventDelegate<Listener> = dispatcher


    /**
     * Calculate password hash
     * @param email User email
     * @param password User password
     */
    private fun hashPassword(email: String, password: String): String {
        // Backend specific salt. This one shouldn't be reused on other devices
        val salt = "27abf393a822078603768c78de67e4a3"

        val m = DigestType.SHA1.getInstance()
        m.update(salt.parseHex())
        m.update(email.toByteArray())
        m.update(password.toByteArray())
        return m.digest().toHexString()
    }

    /**
     * Mobile authorization request
     */
    override fun authorizeMobile(request: AuthorizationService.MobileRequest): AuthorizationService.MobileResponse {
        val serial = request.mobile?.serial
        val imei = request.mobile?.imei

        if (serial.isNullOrEmpty() || imei.isNullOrEmpty())
            throw DefaultProblem(title = "At least one of serial or imei must be provided")

        val user = request.user

        if (user == null)
            throw DefaultProblem(title = "User is required")

        val userRecord = this.userRepository.findByMail(email = user.email)

        if (userRecord == null)
            throw DefaultProblem(title = "User does not exist")

        // Verify credentials
        val hashedPassword = this.hashPassword(
                email = user.email,
                password = user.password)

        if (userRecord.password != hashedPassword)
            throw DefaultProblem(
                    title = "User authentication failed",
                    status = Response.Status.UNAUTHORIZED)

        val identityFactory = MobileIdentityFactory(
                serial = serial ?: "",
                imei = imei ?: ""
        )

        val identity = identityFactory.create()

        return AuthorizationService.MobileResponse(
                key = identity.key.value
        )
    }

    /**
     *
     */
    override fun onMessage(message: AuthorizationService.NodeRequest, replyChannel: sx.jms.Channel?) {
        try {
            log.info(message)

            // Response message
            val am = AuthorizationService.NodeResponse()
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
                val isAuthorized = record.authorized != null && record.authorized != 0

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
