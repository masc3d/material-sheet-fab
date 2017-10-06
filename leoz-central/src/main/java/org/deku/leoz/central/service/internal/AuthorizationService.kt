package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.repository.UserJooqRepository.Companion.verifyPassword
import org.deku.leoz.identity.Identity
import sx.rs.DefaultProblem
import org.deku.leoz.service.internal.AuthorizationService
import org.deku.leoz.model.UserRole
import org.slf4j.LoggerFactory
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener
import sx.mq.MqHandler
import sx.logging.slf4j.info
import sx.time.toTimestamp
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Authorization service
 * Created by masc on 01.07.15.
 */
@Named
@Path("internal/v1/authorize")
class AuthorizationService
    :
        AuthorizationService,
        MqHandler<AuthorizationService.NodeRequest> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Inject
    private lateinit var userRepository: UserJooqRepository

    @Inject
    private lateinit var keyRepository: KeyJooqRepository


    interface Listener : EventListener {
        fun onAuthorized(nodeIdentityUid: Identity.Uid)
    }

    // TODO: replace with rx event
    private val eventSubject = EventDispatcher.createThreadSafe<Listener>()
    val event: EventDelegate<Listener> = eventSubject

    /**
     * Mobile authorization request
     */
    override fun authorizeMobile(request: AuthorizationService.MobileRequest): AuthorizationService.Response {
        val serial = request.mobile?.serial
        val imei = request.mobile?.imei

        if (serial.isNullOrEmpty() || imei.isNullOrEmpty())
            throw DefaultProblem(title = "At least one of serial or imei must be provided")

        // TODO: handle mobile info as required
        
        val user = request.user

        user ?:
                throw DefaultProblem(title = "User is required")

        return authorize(AuthorizationService.Credentials(user.email, user.password))
    }

    override fun authorize(request: AuthorizationService.Credentials): AuthorizationService.Response {
        val user = request

        val userRecord = this.userRepository.findByMail(email = user.email)

        userRecord ?:
                throw DefaultProblem(title = "User does not exist")

        // Verify credentials
        if (!userRecord.verifyPassword(user.password))
            throw DefaultProblem(
                    title = "User authentication failed",
                    status = Response.Status.UNAUTHORIZED)

        val df = DecimalFormat("#")
        df.maximumFractionDigits = 0

        if (!UserRole.values().any { it.name == userRecord.role })
            throw DefaultProblem(
                    title = "Invalid user role",
                    status = Response.Status.UNAUTHORIZED)

        if (!userRecord.isActive) {
            throw DefaultProblem(
                    title = "user deactivated",
                    status = Response.Status.UNAUTHORIZED)
        }
        if (Date() > userRecord.expiresOn) {
            throw DefaultProblem(
                    title = "user account expired",
                    status = Response.Status.UNAUTHORIZED)
        }

        var keyRecord = this.keyRepository.findByID(userRecord.keyId)

        if (keyRecord == null) {
            val keyID = this.keyRepository.insertNew(
                    key = UUID.randomUUID().toString())

            this.userRepository.updateKeyIdById(userRecord.id, keyID)

            keyRecord = this.keyRepository.findByID(keyID)
        }
        userRecord.tsLastlogin = Date().toTimestamp()
        userRecord.store()
//        // TODO: in case we really want to verify key/content validity. but I believe we don't need it.
//        val isValid = try { UUID.fromString(keyRecord.key); true } catch(e: Throwable) { false }

        if (keyRecord == null)
            throw DefaultProblem(title = "User key not valid")

        return AuthorizationService.Response(
                key = keyRecord.key,
                user = userRecord.toUser())
    }

    /**
     *
     */
    override fun onMessage(message: AuthorizationService.NodeRequest, replyChannel: sx.mq.MqChannel?) {
        try {
            log.info(message)

            // Response message
            val am = AuthorizationService.NodeResponse()
            am.key = message.key

            val identityKey = Identity.Uid(message.key)
            var record = nodeJooqRepository.findByKey(message.key)

            if (record == null) {
                // Store new node record
                record = nodeJooqRepository.createNew()
                record.key = message.key
                record.bundle = message.name
            } else {
                // Update record
                record.bundle = message.name

                val isAuthorized = record.authorized != null && record.authorized != 0

                if (isAuthorized)
                    this.eventSubject.emit { it.onAuthorized(identityKey) }

                if (replyChannel != null) {
                    am.authorized = isAuthorized
                    replyChannel.send(am)
                    log.info("Sent authorization [%s]".format(am))
                }
            }
            record.tsLastlogin = Date().toTimestamp()
            record.store()

        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
