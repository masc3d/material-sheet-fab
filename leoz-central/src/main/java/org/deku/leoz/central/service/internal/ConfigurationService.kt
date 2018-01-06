package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.JooqUserRepository
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.service.internal.ConfigurationService
import org.deku.leoz.service.internal.UserService
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import sx.rs.DefaultProblem
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

@Named
@Path("internal/v1/configuration")
class ConfigurationService: ConfigurationService {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Context
    private lateinit var httpHeaders: HttpHeaders

    override fun getUserConfiguration(userId: Int): String {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
        apiKey ?:
                throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        val targetUserRecord = userRepository.findById(userId) ?: throw DefaultProblem(title = "User not found", status = Response.Status.NOT_FOUND)

        if (targetUserRecord.keyId != authorizedUserRecord.keyId) {
            if (UserRole.valueOf(authorizedUserRecord.role) != UserRole.ADMIN) {
                if (authorizedUserRecord.debitorId != targetUserRecord.debitorId) {
                    throw DefaultProblem(title = "No access to this user", status = Response.Status.FORBIDDEN)
                } else {
                    val authRole = UserRole.valueOf(authorizedUserRecord.role)
                    val targetRole = UserRole.valueOf(targetUserRecord.role)
                    if (authRole != UserRole.POWERUSER) {
                        if (targetRole >= authRole) {
                            throw DefaultProblem(title = "No access to this user", status = Response.Status.FORBIDDEN)
                        }
                    }
                }
            }
        }

        return try {
            targetUserRecord.config.toString()
        } catch (e: Exception) {
            "{}"
        }
    }

    override fun getNodeConfiguration(nodeUid: String): String {
        val node = nodeJooqRepository.findByKey(nodeUid) ?: throw DefaultProblem(title = "Invalid Node UID", detail = "Node UID could not be found", status = Response.Status.NOT_FOUND)

        return if (node.configuration.isNullOrEmpty()) "{}" else node.configuration
    }

}