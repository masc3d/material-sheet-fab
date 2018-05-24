package org.deku.leoz.central.service.internal

import org.deku.leoz.central.Application
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
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.rs.RestProblem
import javax.inject.Inject
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

@Component
@Profile(Application.PROFILE_CENTRAL)
@Path("internal/v1/configuration")
class ConfigurationService : org.deku.leoz.node.service.internal.ConfigurationService() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var userRepository: JooqUserRepository

    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    override fun getUserConfiguration(userId: Int): String {
        val targetUserRecord = userRepository.findById(userId)
                ?: throw RestProblem(title = "User not found", status = Response.Status.NOT_FOUND)

        return targetUserRecord.config?.toString() ?: "{}"
    }

    override fun getNodeConfiguration(nodeUid: String): String {
        val node = nodeJooqRepository.findByKey(nodeUid)
                ?: throw NoSuchElementException("Invalid node uid")

        return node.config?.toString() ?: "{}"
    }

    override fun putUserConfiguration(userId: Int, config: String) {
        assertValidJson(config)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putNodeConfiguration(nodeUid: String, config: String) {
        assertValidJson(config)

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}