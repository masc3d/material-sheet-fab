package org.deku.leoz.central.service.internal

import org.deku.leoz.central.Application
import org.deku.leoz.central.data.repository.JooqNodeRepository
import sx.rs.RestProblem
import org.deku.leoz.node.service.internal.BundleServiceV2
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.zalando.problem.Status
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 */
@Named
@Profile(org.deku.leoz.central.Application.Companion.PROFILE_CENTRAL)
@Path("internal/v1/bundle")
open class BundleServiceV1 : org.deku.leoz.node.service.internal.BundleServiceV1() {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Central db node table repository */
    @Inject
    private lateinit var nodeJooqRepository: JooqNodeRepository

    /**
     * Determine alias by node key
     */
    override fun aliasByNodeKey(nodeKey: String): String {
        val rNode = nodeJooqRepository.findByKey(nodeKey)
        if (rNode == null)
            throw RestProblem(
                    title = "Unknown node key [${nodeKey}]",
                    status = Status.NOT_FOUND)

        return rNode.versionAlias
    }
}

/**
 * Bundle service (leoz-central)
 * Created by masc on 01/11/2016.
 */
@Named
@Profile(Application.PROFILE_CENTRAL)
@Path("internal/v2/bundle")
class BundleServiceV2 : BundleServiceV2() {}