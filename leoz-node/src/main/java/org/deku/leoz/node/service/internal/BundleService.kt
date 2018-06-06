package org.deku.leoz.node.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.node.Application
import org.deku.leoz.node.config.UpdateConfiguration
import org.deku.leoz.node.data.jpa.QMstBundleVersion
import org.deku.leoz.node.data.repository.BundleVersionRepository
import org.deku.leoz.node.data.repository.NodeRepository
import org.deku.leoz.service.internal.entity.update.UpdateInfo
import org.deku.leoz.service.internal.update.BundleUpdateService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.zalando.problem.Status
import sx.packager.BundleRepository
import sx.platform.OperatingSystem
import sx.rs.RestProblem
import sx.rs.attachment
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response


/**
 * BundleService implementation
 * Created by masc on 01/11/2016.
 */
@Component
@Path("internal/v2/bundle")
@Api(value = "Bundle operations")
open class BundleServiceV2 : org.deku.leoz.service.internal.BundleServiceV2 {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    /** Leoz bundle repository */
    @Inject
    private lateinit var bundleRepository: BundleRepository

    @Inject
    private lateinit var nodeRepo: NodeRepository

    @Inject
    private lateinit var bundleUpdateService: BundleUpdateService

    @Inject
    private lateinit var updateConfiguration: UpdateConfiguration

    @Context
    private lateinit var response: HttpServletResponse

    /**
     * Look up version alias by node key
     */
    private fun aliasByNodeKey(nodeKey: String): String {
        val rNode = nodeRepo.findByUid(nodeKey)
        if (rNode == null)
            throw RestProblem(
                    title = "Unknown node key [${nodeKey}]",
                    status = Status.NOT_FOUND)

        return rNode.versionAlias
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


        return Response.ok()
                .attachment(downloadFile)
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
