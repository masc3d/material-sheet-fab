package org.deku.leoz.node.service.internal

import com.querydsl.core.BooleanBuilder
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.node.data.jpa.MstNode
import org.deku.leoz.node.data.jpa.QMstNode.mstNode
import org.deku.leoz.node.data.repository.NodeRepository
import org.deku.leoz.service.internal.ConfigurationService
import org.deku.leoz.service.internal.NodeServiceV1
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.persistence.transaction
import sx.time.toTimestamp
import sx.util.letWithNotNull
import sx.util.toNullable
import java.util.*
import javax.inject.Inject
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceUnit
import javax.ws.rs.Path
import kotlin.NoSuchElementException


/**
 * Created by masc on 17.02.16.
 */
@Component
@Path("internal/v1/node")
class NodeServiceV1
    :
        org.deku.leoz.service.internal.NodeServiceV1,
        MqHandler<NodeServiceV1.Info> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var nodeRepo: NodeRepository

    @Inject
    private lateinit var configurationService: ConfigurationService

    @PersistenceUnit(name = org.deku.leoz.node.config.PersistenceConfiguration.QUALIFIER)
    private lateinit var emf: EntityManagerFactory

    override fun onMessage(message: NodeServiceV1.Info, replyChannel: MqChannel?) {
        try {
            emf.transaction { em ->
                val nodeInfo = message

                log.info { nodeInfo }

                val identityUid = Identity.Uid(nodeInfo.uid)

                var rNode = nodeRepo.findOne(
                        mstNode.uid.eq(identityUid.uuid)
                ).toNullable()

                if (rNode == null) {
                    // Store new node record
                    rNode = MstNode().also {
                        it.uid = identityUid.uuid
                        it.versionAlias = "release"
                    }
                }

                rNode.sysInfo = nodeInfo.systemInformation
                rNode.bundle = nodeInfo.bundleName
                rNode.currentVersion = nodeInfo.bundleVersion
                rNode.serial = nodeInfo.hardwareSerialNumber
                rNode.tsLastlogin = Date().toTimestamp()
                rNode.tsModified = Date().toTimestamp()

                em.merge(rNode)
            }
        } catch (e: Throwable) {
            log.error("Could not process node info [${message}]: ${e.message}")
        }
    }

    override fun get(id: List<Int>?): List<NodeServiceV1.Node> {
        return nodeRepo.findAll(BooleanBuilder()
                .letWithNotNull(id, {
                    if (it.count() > 0)
                        and(mstNode.id.`in`(it.map { it.toLong() }))
                    else
                        this
                })
        )
                .map { it.toNode() }
    }

    override fun getByUid(uid: String): NodeServiceV1.Node {
        return nodeRepo.findByUid(uid, strict = false)
                ?.toNode()
                ?: throw NoSuchElementException("Unknown node uid [${uid}]")
    }

    override fun requestDiagnosticData(nodeUid: String) {
        val rNode = this.nodeRepo.findByUid(nodeUid, strict = false)
                ?: throw NoSuchElementException("Unknown node uid [${nodeUid}]")

        JmsEndpoints.node.topic(Identity.Uid(rNode.uid.toString())).channel().use {
            it.send(NodeServiceV1.DiagnosticDataRequest())
        }
    }

    override fun getConfiguration(nodeUid: String): String {
        return configurationService.getNodeConfiguration(nodeUid)
    }

    fun MstNode.toNode(): NodeServiceV1.Node {
        return NodeServiceV1.Node(
                id = this.id,
                uid = this.uid.toString()
        )
    }
}
