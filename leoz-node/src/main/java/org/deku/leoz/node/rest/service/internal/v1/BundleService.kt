package org.deku.leoz.node.rest.service.internal.v1

import org.deku.leoz.bundle.BundleType
import sx.packager.BundleRepository
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.UpdateConfiguration
import org.deku.leoz.service.update.UpdateInfo
import org.deku.leoz.service.update.UpdateInfoRequest
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repository.master.BundleVersionRepository
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import sx.platform.OperatingSystem
import sx.rs.ApiKey
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Path
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Context

/**
 * Created by masc on 01/11/2016.
 */
@Named
@ApiKey(false)
@Profile(Application.PROFILE_CLIENT_NODE)
@Path("internal/v1/bundle")
open class BundleService : org.deku.leoz.rest.service.internal.v1.BundleService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    /** Leoz bundle repository */
    @Inject
    private lateinit var bundleRepository: BundleRepository

    @Inject
    private lateinit var updateConfiguration: UpdateConfiguration

    @Context
    private lateinit var response: HttpServletResponse

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
            if (nodeKey == null) {
                val instanceVersionAlias = this.updateConfiguration.versionAlias
                if (instanceVersionAlias.isNullOrEmpty())
                    throw IllegalArgumentException("Missing criteria")

                versionAlias = instanceVersionAlias
            } else {
                versionAlias = this.aliasByNodeKey(nodeKey)
            }
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

    /**
     * @see org.deku.leoz.rest.service.internal.v1.BundleService
     */
    override fun donwload(bundleName: String, version: String): File {
        if (!this.bundleRepository.rsyncModuleUri.isFile())
            throw WebApplicationException("Bundle repository is not local [${this.bundleRepository.rsyncModuleUri}]")

        val downloadFile = File(this.bundleRepository.rsyncModuleUri.uri)
                .resolve(bundleName)
                .resolve(version)
                .resolve(OperatingSystem.ANDROID.toString())
                .resolve("${bundleName}-${version}.apk")

        if (downloadFile.exists())
            response.setHeader("Content-Disposition", "attachment; filename=\"${downloadFile.name}\"")

        return downloadFile
    }
}