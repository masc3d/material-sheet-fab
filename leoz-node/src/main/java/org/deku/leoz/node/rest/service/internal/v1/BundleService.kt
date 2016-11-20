package org.deku.leoz.node.rest.service.internal.v1

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.node.App
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.service.update.UpdateInfoRequest
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
 * Created by masc on 01/11/2016.
 */
@Named
@ApiKey(false)
@Profile(App.PROFILE_CLIENT_NODE)
@Path("internal/v1/bundle")
open class BundleService : org.deku.leoz.rest.service.internal.v1.BundleService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    /** Leoz bundle repository */
    @Inject
    private lateinit var bundleRepository: BundleRepository

    /**
     * Look up version alias by node key
     */
    open fun aliasByNodeKey(nodeKey: String): String {
        // Implemented only in leoz-central
        throw UnsupportedOperationException("Node key lookup not supported")
    }

    /**
     * @see org.deku.leoz.rest.service.internal.v1.BundleService
     */
    override fun info(bundleName: String, versionAlias: String?, nodeKey: String?): UpdateInfo {
        @Suppress("NAME_SHADOWING")
        var versionAlias = versionAlias
        if (versionAlias == null) {
            if (nodeKey == null)
                throw IllegalArgumentException("Missing criteria")

            versionAlias = this.aliasByNodeKey(nodeKey)
        }

        val qTable = QMstBundleVersion.mstBundleVersion
        val rVersion = bundleVersionRepository.findOne(
                qTable.bundle.eq(bundleName)
                        .and(qTable.alias.eq(versionAlias)))

        if (rVersion == null)
            throw IllegalArgumentException("No version record for bundle [${bundleName}] version alias [${versionAlias}]")

        // Try to determine latest matching bundle version and platforms
        val latestDesignatedVersion = try {
            this.bundleRepository.queryLatestMatchingVersion(bundleName, rVersion.version)
        } catch (e: NoSuchElementException) {
            log.warn(e.message)
            null
        }

        val latestDesignatedVersionPlatforms =
                (if (latestDesignatedVersion != null)
                    this.bundleRepository.listPlatforms(bundleName, latestDesignatedVersion)
                else
                    ArrayList<String>())

        return UpdateInfo(
                bundleName = rVersion.bundle,
                bundleVersionAlias = rVersion.alias,
                bundleVersionPattern = rVersion.version,
                latestDesignatedVersion = latestDesignatedVersion?.toString(),
                latestDesignatedVersionPlatforms = latestDesignatedVersionPlatforms.map { it.toString() }.toTypedArray())
    }
}