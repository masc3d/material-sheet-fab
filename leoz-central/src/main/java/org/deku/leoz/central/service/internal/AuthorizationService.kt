package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.repository.*
import org.deku.leoz.central.data.repository.JooqUserRepository.Companion.verifyPassword
import org.deku.leoz.identity.Identity
import org.deku.leoz.model.UserRole
import org.deku.leoz.service.internal.AuthorizationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.log.slf4j.info
import sx.mq.MqHandler
import sx.rs.RestProblem
import sx.time.toTimestamp
import sx.util.toUUID
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Authorization service
 * Created by masc on 01.07.15.
 */
@Component
@Path("internal/v1/authorize")
class AuthorizationService
    :
        AuthorizationService,
        MqHandler<AuthorizationService.NodeRequest> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var keyRepository: JooqKeyRepository

    @Inject
    private lateinit var stationJooqRepository: JooqStationRepository

    //region REST
    /**
     * Authorize user
     */
    override fun authorizeUser(request: AuthorizationService.Credentials): AuthorizationService.Response {
        val user = request

        val userRecord = this.userRepository.findByMail(email = user.email)

        userRecord ?: throw RestProblem(title = "User does not exist")

        // Verify credentials
        if (!userRecord.verifyPassword(user.password))
            throw RestProblem(
                    title = "User authentication failed",
                    status = Response.Status.UNAUTHORIZED)

        val df = DecimalFormat("#")
        df.maximumFractionDigits = 0

        if (!UserRole.values().any { it.name == userRecord.role })
            throw RestProblem(
                    title = "Invalid user role",
                    status = Response.Status.UNAUTHORIZED)

        if (!userRecord.isActive) {
            throw RestProblem(
                    title = "User deactivated",
                    status = Response.Status.UNAUTHORIZED)
        }
        if (Date() > userRecord.expiresOn) {
            throw RestProblem(
                    title = "User account expired",
                    status = Response.Status.UNAUTHORIZED)
        }

        var keyRecord = userRecord.keyUid?.let {
            this.keyRepository.findByUid(it.toUUID())
        }

        if (keyRecord == null) {
            val keyUid = this.keyRepository.insertNew(
                    key = UUID.randomUUID().toString())

            this.userRepository.updateKeyIdById(userRecord.id, keyUid)

            keyRecord = this.keyRepository.findByUid(keyUid)
        }
        userRecord.tsLastlogin = Date().toTimestamp()
        userRecord.store()
//        // TODO: in case we really want to verify key/content validity. but I believe we don't need it.
//        val isValid = try { UUID.fromString(keyRecord.key); true } catch(e: Throwable) { false }

        if (keyRecord == null)
            throw RestProblem(title = "User key not valid")

        return AuthorizationService.Response(
                key = keyRecord.key,
                user = userRecord.toUser().also { x -> x.allowedStations = stationJooqRepository.findAllowedStationsByUserId(userRecord.id) })
    }

    override fun authorizeWeb(request: AuthorizationService.Credentials): AuthorizationService.Response {
        return this.authorizeUser(request)
    }

//endregion

//region MQ
    /**
     * Node authorizatino request mq handler
     */
    override fun onMessage(message: AuthorizationService.NodeRequest, replyChannel: sx.mq.MqChannel?) {
        try {
            log.info { message }

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
//endregion
}
