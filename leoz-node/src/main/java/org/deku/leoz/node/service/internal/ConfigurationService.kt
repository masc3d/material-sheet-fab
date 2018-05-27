package org.deku.leoz.node.service.internal

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.config.MqEndpoints
import org.deku.leoz.config.Rest
import org.deku.leoz.identity.Identity
import org.deku.leoz.identity.toIdentityUid
import org.deku.leoz.model.UserRole
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.PersistenceConfiguration
import org.deku.leoz.node.data.jpa.QMstUser.mstUser
import org.deku.leoz.node.data.repository.NodeRepository
import org.deku.leoz.node.data.repository.UserRepository
import org.deku.leoz.node.rest.authorizedUser
import org.deku.leoz.node.rest.isAvailable
import org.deku.leoz.service.internal.UserService
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import sx.mq.jms.channel
import sx.mq.jms.toJms
import sx.rs.RestProblem
import sx.util.toNullable
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import kotlin.NoSuchElementException

/**
 * Configuration service
 *
 * Created by masc on 17.03.18.
 */
@Component
@Profile(Application.PROFILE_CLIENT_NODE)
@Path("internal/v1/configuration")
class ConfigurationService : org.deku.leoz.service.internal.ConfigurationService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var nodeRepository: NodeRepository

    /** Jackson object mapper */
    private val mapper by lazy {
        ObjectMapper().also {
            it.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
        }
    }

    /**
     * Assert generic json integrity
     */
    protected fun assertValidJson(json: String) {
        // Read tree for verification
        this.mapper.readTree(json)
    }

    //region REST
    override fun getUserConfiguration(userId: Int): String {
        val targetUserRecord = userRepository.findById(userId.toLong())
                .toNullable()
                ?: throw RestProblem(title = "User not found", status = Response.Status.NOT_FOUND)

        return targetUserRecord.config?.toString() ?: "{}"
    }

    override fun getNodeConfiguration(nodeUid: String): String {
        val uid: UUID = try {
            UUID.fromString(nodeUid)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid node uid")
        }

        val node = nodeRepository.findByUid(uid)
                ?: throw NoSuchElementException("Invalid node uid")

        return node.config?.toString() ?: "{}"
    }

    override fun putUserConfiguration(userId: Int, config: String) {
        assertValidJson(config)

        userRepository.findById(userId.toLong())
                .toNullable()
                ?.also {
                    it.config = config
                    userRepository.save(it)
                }
                ?: throw NoSuchElementException("Invalid user")
    }

    override fun putNodeConfiguration(nodeUid: String, config: String) {
        val uid: UUID = try {
            UUID.fromString(nodeUid)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid node uid")
        }

        assertValidJson(config)

        nodeRepository.findByUid(uid)
                ?.also {
                    it.config = config
                    nodeRepository.save(it)
                }
                ?: throw NoSuchElementException("Invalid node uid")
    }
    //endregion
}
