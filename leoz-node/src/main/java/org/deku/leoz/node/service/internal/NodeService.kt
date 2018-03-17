package org.deku.leoz.node.service.internal

import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.node.Application
import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.deku.leoz.node.data.repository.NodeRepository
import org.deku.leoz.service.internal.ConfigurationService
import org.deku.leoz.service.internal.NodeServiceV1
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.persistence.transaction
import sx.rs.RestProblem
import sx.time.toTimestamp
import sx.util.toNullable
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.ws.rs.Path


/**
 * Created by masc on 17.02.16.
 */
@Profile(Application.PROFILE_CLIENT_NODE)
@Component
@Path("internal/v1/node")
class NodeServiceV1
    :
        org.deku.leoz.service.internal.NodeServiceV1,
        MqHandler<NodeServiceV1.Info> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeRepository: NodeRepository

    @Inject
    private lateinit var configurationService: ConfigurationService

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

    override fun onMessage(message: NodeServiceV1.Info, replyChannel: MqChannel?) {
        emf.transaction { em ->
            val nodeInfo = message

            log.info { nodeInfo }

            val identityUid = Identity.Uid(nodeInfo.uid)
            var rNode = nodeRepository.findOne(
                    mstNode.key.eq(identityUid.value)
            ).toNullable()

            if (rNode == null) {
                // Store new node record
                rNode = MstNode().also {
                    it.key = identityUid.value
                }
            }

            rNode.serial = nodeInfo.hardwareSerialNumber
            rNode.currentVersion = nodeInfo.bundleVersion
            rNode.tsLastlogin = Date().toTimestamp()
            rNode.bundle = nodeInfo.bundleName
            rNode.sysInfo = nodeInfo.systemInformation

            em.merge(rNode)
        }

    }

    override fun requestDiagnosticData(nodeUid: String) {
        val rNode = this.nodeRepository.findByUid(nodeUid)
                ?: throw RestProblem(status = Status.NOT_FOUND)

        JmsEndpoints.node.topic(Identity.Uid(rNode.key)).channel().use {
            it.send(NodeServiceV1.DiagnosticDataRequest())
        }
    }

    override fun getConfiguration(nodeUid: String): String {
        return configurationService.getNodeConfiguration(nodeUid)
    }

}
