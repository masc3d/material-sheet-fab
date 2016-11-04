package org.deku.leoz.central.rest.services.internal.v1

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.update.UpdateInfo
import org.deku.leoz.bundle.update.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.NodeJooqRepository
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repositories.master.BundleVersionRepository
import org.slf4j.LoggerFactory
import sx.rs.ApiKey
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Bundle service leoz-central
 * Created by masc on 01/11/2016.
 */
@Named
@ApiKey(false)
@Path("internal/v1/bundle")
class BundleService : org.deku.leoz.node.rest.services.internal.v1.BundleService() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Central db node table repository */
    @Inject
    private lateinit var nodeJooqRepository: NodeJooqRepository

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    /** Leoz bundle repository */
    @Inject
    private lateinit var bundleRepository: BundleRepository

    /**
     * @see org.deku.leoz.rest.services.internal.v1.BundleService
     */
    override fun info(request: UpdateInfoRequest): UpdateInfo {
        // Determine version alias
        val versionAlias: String
        if (request.versionAlias.isNotEmpty()) {
            // Primarily use version alias of request, if provided
            versionAlias = request.versionAlias
        } else {
            // Lookup appropriate version alias by node key
            if (request.nodeKey.isNullOrEmpty())
                throw IllegalArgumentException("Neither version alias nor node key provided [${request}]")

            val rNode = nodeJooqRepository.findByKey(request.nodeKey)
            if (rNode == null)
                throw IllegalArgumentException("Unknown node [${request.nodeKey}}")

            versionAlias = rNode.versionAlias
        }

        return this.info(request.bundleName, versionAlias)
    }
}