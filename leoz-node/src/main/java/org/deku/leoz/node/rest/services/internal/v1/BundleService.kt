package org.deku.leoz.node.rest.services.internal.v1

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.update.UpdateInfo
import org.deku.leoz.bundle.update.UpdateInfoRequest
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repositories.master.BundleVersionRepository
import org.slf4j.LoggerFactory
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
@Path("internal/v1/bundle")
open class BundleService : org.deku.leoz.rest.services.internal.v1.BundleService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    /** Leoz bundle repository */
    @Inject
    private lateinit var bundleRepository: BundleRepository

    /**
     * @see org.deku.leoz.rest.services.internal.v1.BundleService
     */
    override fun info(bundleName: String, versionAlias: String): UpdateInfo {
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

    /**
     * @see org.deku.leoz.rest.services.internal.v1.BundleService
     */
    override fun info(request: UpdateInfoRequest): UpdateInfo {
        // Implemented only in leoz-central
        throw UnsupportedOperationException("not implemented")
    }
}