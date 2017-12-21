package org.deku.leoz.central.service.internal

import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.data.repository.UserJooqRepository
import org.deku.leoz.config.Rest
import org.deku.leoz.service.internal.ConfigurationServiceV1
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
class ConfigurationServiceV1: ConfigurationServiceV1 {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dslContext: DSLContext

    @Inject
    private lateinit var userService: UserService

    @Inject
    private lateinit var userRepository: UserJooqRepository

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Context
    private lateinit var httpHeaders: HttpHeaders

    override fun getCurrentUserConfiguration(): String? {
        val apiKey = this.httpHeaders.getHeaderString(Rest.API_KEY)
        apiKey ?:
                throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        val authorizedUserRecord = userRepository.findByKey(apiKey)
        authorizedUserRecord ?:
                throw DefaultProblem(status = Response.Status.UNAUTHORIZED)

        return try {
            authorizedUserRecord.config.toString()
        } catch (e: Exception) {
            "{}"
        }
    }

    override fun getNodeConfiguration(nodeKey: String): String? {
        val node = nodeJooqRepository.findByKey(nodeKey) ?: throw DefaultProblem(title = "Invalid DeviceID", detail = "Device ID could not be found", status = Response.Status.NOT_FOUND)

        return if (node.configuration.isNullOrEmpty()) "{}" else node.configuration
    }

}