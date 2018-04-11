package org.deku.leoz.node.service.internal

import org.deku.leoz.identity.Identity
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.jpa.MstKey
import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.repository.*
import org.deku.leoz.service.internal.AuthorizationService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.log.slf4j.info
import sx.mq.MqHandler
import sx.rs.RestProblem
import sx.time.toTimestamp
import sx.util.toNullable
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * Authorization service
 * Created by masc on 17.03.18.
 */
@Profile(Application.PROFILE_CLIENT_NODE)
@Component
@Path("internal/v1/authorize")
class AuthorizationService
    :
        AuthorizationService,
        MqHandler<AuthorizationService.NodeRequest> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeRepo: NodeRepository

    @Inject
    private lateinit var userRepo: UserRepository

    @Inject
    private lateinit var keyRepo: KeyRepository

    @Inject
    private lateinit var stationRepository: StationRepository

    //region REST
    /**
     * Authorize user
     */
    override fun authorize(request: AuthorizationService.Credentials): AuthorizationService.Response {
        val user = request

        val userRecord = this.userRepo.findByEmail(email = user.email)
                ?: throw RestProblem(title = "User does not exist")

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
                    title = "user deactivated",
                    status = Response.Status.UNAUTHORIZED)
        }
        if (Date() > userRecord.expiresOn) {
            throw RestProblem(
                    title = "user account expired",
                    status = Response.Status.UNAUTHORIZED)
        }

        val keyRecord = this.keyRepo.findById(userRecord.keyId)
                .toNullable()
                ?: MstKey().also {
                    it.key = UUID.randomUUID().toString()
                    it.timestamp = Date().toTimestamp()
                    it.type = "user"

                    keyRepo.save(it)

                    userRecord.keyId = it.id
                }

        userRecord.tsLastlogin = Date().toTimestamp()
        userRepo.save(userRecord)

//        // TODO: in case we really want to verify key/content validity. but I believe we don't need it.
//        val isValid = try { UUID.fromString(keyRecord.key); true } catch(e: Throwable) { false }


        return AuthorizationService.Response(
                key = keyRecord.key,
                user = userRecord.toUser().also { x -> x.allowedStations = stationRepository.findAllowedStationsByUserId(userRecord.id) }
        )
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

            var nodeRecord = nodeRepo.findByUid(UUID.fromString(message.key))

            if (nodeRecord == null) {
                // Store new node record
                nodeRecord = MstNode().also {
                    it.uid = UUID.fromString(message.key)
                    it.bundle = message.name
                    it.versionAlias = "release"
                    it.tsModified = Date().toTimestamp()
                }
            } else {
                // Update record
                nodeRecord.bundle = message.name

                val isAuthorized = nodeRecord.authorized != null && nodeRecord.authorized != 0

                if (replyChannel != null) {
                    am.authorized = isAuthorized
                    replyChannel.send(am)
                    log.info("Sent authorization [%s]".format(am))
                }
            }
            nodeRecord.tsLastlogin = Date().toTimestamp()

            nodeRepo.save(nodeRecord)

        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
    //endregion
}
