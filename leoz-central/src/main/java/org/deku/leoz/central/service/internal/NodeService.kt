package org.deku.leoz.central.service.internal

import org.deku.leoz.central.Application
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.repository.JooqNodeRepository
import org.deku.leoz.central.data.repository.fetchByUid
import org.deku.leoz.central.data.repository.uid
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.service.internal.NodeServiceV1
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.rs.RestProblem
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.ws.rs.Path

/**
 * Created by masc on 17.02.16.
 */
@Component
@Path("internal/v1/node")
@Profile(Application.PROFILE_CENTRAL)

class NodeServiceV1
    :
        org.deku.leoz.service.internal.NodeServiceV1,
        MqHandler<NodeServiceV1.Info>
{

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    @Qualifier(PersistenceConfiguration.QUALIFIER)
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    @Inject
    private lateinit var configurationService: ConfigurationService

    override fun onMessage(message: NodeServiceV1.Info, replyChannel: MqChannel?) {
        val nodeInfo = message

        log.info { nodeInfo }

        val identityUid = Identity.Uid(nodeInfo.uid)
        var rNode = nodeJooqRepository.findByKey(identityUid.value)

        if (rNode == null) {
            // Store new node record
            rNode = nodeJooqRepository.createNew()
            rNode.key = identityUid.value
        }

        rNode.serial = nodeInfo.hardwareSerialNumber
        rNode.currentVersion = nodeInfo.bundleVersion
        rNode.tsLastlogin = Date().toTimestamp()
        rNode.bundle = nodeInfo.bundleName
        rNode.sysInfo = nodeInfo.systemInformation
        rNode.store()
    }

    override fun requestDiagnosticData(nodeUid: String) {
        val rNode = dsl.
                selectFrom(Tables.MST_NODE)
                .fetchByUid(
                        nodeUid = nodeUid,
                        strict = false)

                ?: throw RestProblem(status = Status.NOT_FOUND)

        JmsEndpoints.node.topic(Identity.Uid(rNode.uid)).channel().use {
            it.send(NodeServiceV1.DiagnosticDataRequest())
        }
    }

    override fun getConfiguration(nodeUid: String): String {
        return configurationService.getNodeConfiguration(nodeUid)
    }

}
