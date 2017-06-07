package org.deku.leoz.central.service.internal

import org.apache.commons.lang3.RandomStringUtils
import org.deku.leoz.central.data.repository.KeyJooqRepository
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository.Companion.verifyPassword
import org.deku.leoz.central.data.repository.toAuthorizationServiceUser
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.MobileIdentityFactory
import org.deku.leoz.node.rest.DefaultProblem
import org.deku.leoz.service.internal.AuthorizationService
import org.deku.leoz.model.UserRole
import org.slf4j.LoggerFactory
import sx.event.EventDelegate
import sx.event.EventDispatcher
import sx.event.EventListener
import sx.mq.MqHandler
import sx.logging.slf4j.info
import sx.rs.auth.ApiKey
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
@ApiKey(false)
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
        fun onAuthorized(nodeIdentityKey: Identity.Key)
    }

    // TODO: replace with rx event
    private val dispatcher = EventDispatcher.createThreadSafe<Listener>()
    public val delegate: EventDelegate<Listener> = dispatcher

    /**
     * Mobile authorization request
     */
    override fun authorizeMobile(request: AuthorizationService.MobileRequest): AuthorizationService.WebResponse {
        val serial = request.mobile?.serial
        val imei = request.mobile?.imei

        if (serial.isNullOrEmpty() || imei.isNullOrEmpty())
            throw DefaultProblem(title = "At least one of serial or imei must be provided")

        // TODO: handle mobile info as required

        // TODO: when response is unified, we could simply delegate to `authorizeWeb` from here (and make it generic)

        val user = request.user

        user ?:
            throw DefaultProblem(title = "User is required")

        return authorizeWeb(AuthorizationService.Credentials(user.email,user.password))
    }

    override fun authorizeWeb(request: AuthorizationService.Credentials): AuthorizationService.WebResponse {
        val user = request

        val userRecord = this.userRepository.findByMail(email = user.email)

        userRecord ?:
            throw DefaultProblem(title = "User does not exist")

        // Verify credentials
        if (!userRecord.verifyPassword(user.password))
            throw DefaultProblem(
                    title = "User authentication failed",
                    status = Response.Status.UNAUTHORIZED)
        val debitorNo = this.userRepository.findDebitorNoById(id = userRecord.debitorId)
                ?: throw DefaultProblem(
                title = "Missing sdebitor",
                status = Response.Status.UNAUTHORIZED)

        val df = DecimalFormat("#")
        df.maximumFractionDigits = 0

        if (!UserRole.values().any { it.name == userRecord.role })
            throw DefaultProblem(
                    title = "Invalid user role",
                    status = Response.Status.UNAUTHORIZED)

        var keyRecord = this.keyRepository.findByID(userRecord.keyId)

        if (keyRecord == null) {
            val keyID = this.keyRepository.insertNew(
                    key = UUID.randomUUID().toString())

            this.userRepository.updateKeyIdById(userRecord.id, keyID)

            keyRecord = this.keyRepository.findByID(keyID)
        }

//        // TODO: in case we really want to verify key/content validity. but I believe we don't need it.
//        val isValid = try { UUID.fromString(keyRecord.key); true } catch(e: Throwable) { false }

        if (keyRecord == null)
            throw DefaultProblem(title = "User key not valid")

        return AuthorizationService.WebResponse(
                key = keyRecord.key,
                user=userRecord.toAuthorizationServiceUser())
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
