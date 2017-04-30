package org.deku.leoz.central.service.internal

import org.deku.leoz.central.data.repository.NodeJooqRepository
import org.slf4j.LoggerFactory

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 */
@javax.inject.Named
@sx.rs.auth.ApiKey(false)
@org.springframework.context.annotation.Profile(org.deku.leoz.central.Application.Companion.PROFILE_CENTRAL)
@javax.ws.rs.Path("internal/v1/bundle")
open class BundleService : org.deku.leoz.node.service.internal.BundleService() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Central db node table repository */
    @javax.inject.Inject
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