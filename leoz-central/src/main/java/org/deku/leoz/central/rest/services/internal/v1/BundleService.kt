package org.deku.leoz.central.rest.services.internal.v1

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.central.Application
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.service.update.UpdateInfoRequest
import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repository.master.BundleVersionRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import sx.rs.ApiKey
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 */
@Named
@ApiKey(false)
@Profile(Application.PROFILE_CENTRAL)
@Path("internal/v1/bundle")
class BundleService : org.deku.leoz.node.rest.service.internal.v1.BundleService() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Central db node table repository */
    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    /**
     * Determine alias by node key
     */
    override fun aliasByNodeKey(nodeKey: String): String {
        val rNode = nodeJooqRepository.findByKey(nodeKey)
        if (rNode == null)
            throw IllegalArgumentException("Unknown node key [${nodeKey}]")

        return rNode.versionAlias
    }
}