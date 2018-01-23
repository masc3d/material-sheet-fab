package org.deku.leoz.node.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.UpdateConfiguration
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repository.master.BundleVersionRepository
import sx.rs.RestProblem
import org.deku.leoz.service.internal.update.BundleUpdateService
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import org.deku.leoz.service.internal.BundleServiceV2
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.zalando.problem.Status
import sx.packager.BundleRepository
import sx.platform.OperatingSystem
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


/**
 * BundleService implementation
 * Created by masc on 01/11/2016.
 */
@Named
@Profile(Application.PROFILE_CLIENT_NODE)
@Path("internal/v2/bundle")
@Api(value = "Bundle operations")
open class BundleServiceV2 : BundleServiceV2 {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    /** Leoz bundle repository */
    @Inject
    private lateinit var bundleRepository: BundleRepository

    @Inject
    private lateinit var bundleUpdateService: BundleUpdateService

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
     * @see {@link org.deku.leoz.rest.service.internal.v1.BundleService}
     */
    override fun info(bundleName: String, versionAlias: String?, nodeKey: String?): UpdateInfo {
        @Suppress("NAME_SHADOWING")
        var versionAlias = versionAlias
        if (versionAlias == null) {
            if (nodeKey == null) {
                val instanceVersionAlias = this.updateConfiguration.versionAlias
                if (instanceVersionAlias.isEmpty())
                    throw RestProblem(title = "Missing criteria")

                versionAlias = instanceVersionAlias
            } else {
                versionAlias = this.aliasByNodeKey(nodeKey)
            }
        }

        val qTable = QMstBundleVersion.mstBundleVersion
        val rVersion = bundleVersionRepository.findOne(
                qTable.bundle.eq(bundleName)
                        .and(qTable.alias.eq(versionAlias)))
                .orElse(null)

        if (rVersion == null)
            throw RestProblem(
                    title = "No version record for bundle [${bundleName}] version alias [${versionAlias}]",
                    status = Status.NOT_FOUND)

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
     * @see {@link org.deku.leoz.rest.service.internal.v1.BundleService}
     */
    override fun download(bundleName: String, version: String): Response {
        if (!this.bundleRepository.rsyncModuleUri.isFile())
            throw RestProblem(
                    title = "Bundle repository is not local [${this.bundleRepository.rsyncModuleUri}]",
                    status = Status.INTERNAL_SERVER_ERROR
            )

        val downloadFile = File(this.bundleRepository.rsyncModuleUri.uri)
                .resolve(bundleName)
                .resolve(version)
                .resolve(OperatingSystem.ANDROID.toString())
                .resolve("${bundleName}-${version}.apk")

        if (!downloadFile.exists())
            throw RestProblem(
                    status = Response.Status.NOT_FOUND,
                    title = "No such file")

        return Response
                .ok(downloadFile, MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${downloadFile.name}\"")
                .header(HttpHeaders.CONTENT_LENGTH, downloadFile.length().toString())
                .build()
    }

    override fun downloadLatest(bundleName: String, alias: String, nodeKey: String?): Response {
        val currentBundleInfo = this.info(
                bundleName = bundleName,
                versionAlias = alias,
                nodeKey = nodeKey
        )

        return this.download(
                bundleName = bundleName,
                version = currentBundleInfo.latestDesignatedVersion!!
        )
    }

    override fun clean() {
        this.bundleUpdateService.scheduleCleanup(
                preserve = this.bundleVersionRepository
                        .findAll()
                        .map { BundleRepository.PreserveSpec(name = it.bundle, pattern = it.version) }
        )
    }
}
