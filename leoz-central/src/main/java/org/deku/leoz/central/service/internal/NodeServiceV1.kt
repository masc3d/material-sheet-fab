package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.jooq.dekuclient.Tables
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.central.data.repository.fetchByUid
import org.deku.leoz.central.data.repository.uid
import org.deku.leoz.config.JmsEndpoints
import org.deku.leoz.identity.Identity
import org.deku.leoz.service.entity.ShortDate
import org.deku.leoz.service.internal.NodeServiceV1
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.zalando.problem.Status
import sx.log.slf4j.info
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.mq.jms.channel
import sx.rs.DefaultProblem
import sx.time.toTimestamp
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.*

/**
 * Created by masc on 17.02.16.
 */
@Named
@Path("internal/v1/node")
class NodeServiceV1
    :
        org.deku.leoz.service.internal.NodeServiceV1,
        MqHandler<NodeServiceV1.Info> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var dsl: DSLContext

    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    override fun onMessage(message: NodeServiceV1.Info, replyChannel: MqChannel?) {
        val nodeInfo = message

        log.info(nodeInfo)

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

                ?: throw DefaultProblem(status = Status.NOT_FOUND)

        JmsEndpoints.node.topic(Identity.Uid(rNode.uid)).channel().use {
            it.send(NodeServiceV1.DiagnosticDataRequest())
        }
    }
}
