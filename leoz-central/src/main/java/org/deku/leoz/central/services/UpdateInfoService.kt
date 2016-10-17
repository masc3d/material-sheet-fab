package org.deku.leoz.central.services

import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.update.UpdateInfo
import org.deku.leoz.bundle.update.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.BundleVersionJooqRepository
import org.deku.leoz.central.data.repositories.NodeJooqRepository
import org.slf4j.LoggerFactory
import sx.jms.Channel
import sx.jms.Handler
import java.util.*

/**
 * Update info service, providing version pattern information to clients
 * Created by masc on 19.10.15.
 */
class UpdateInfoService(
        /** Central db node table repository */
        private val nodeJooqRepository: NodeJooqRepository,
        /** Central db bundle version table repository */
        private val bundleVersionJooqRepository: BundleVersionJooqRepository,

        /** Leoz bundle repository */
        private val bundleRepository: BundleRepository
)
:
        Handler<UpdateInfoRequest> {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onMessage(message: UpdateInfoRequest, replyChannel: Channel?) {
        try {
            val updateInfoRequest = message

            // Determine version alias
            val versionAlias: String
            if (updateInfoRequest.versionAlias != null) {
                // Primarily use version alias of request, if provided
                versionAlias = updateInfoRequest.versionAlias!!
            } else {
                // Lookup appropriate version alias by node key
                if (updateInfoRequest.nodeKey.isNullOrEmpty())
                    throw IllegalArgumentException("Neither version alias nor node key provided [${updateInfoRequest}]")

                val rNode = nodeJooqRepository.findByKey(updateInfoRequest.nodeKey)
                if (rNode == null)
                    throw IllegalArgumentException("Unknown node [${updateInfoRequest.nodeKey}}")

                versionAlias = rNode.versionAlias
            }

            val rVersion = bundleVersionJooqRepository.findByAlias(
                    bundleName = updateInfoRequest.bundleName,
                    versionAlias = versionAlias)

            if (rVersion == null)
                throw IllegalArgumentException("No version record for node [${updateInfoRequest.nodeKey}] bundle [${updateInfoRequest.bundleName}] version alias [${versionAlias}]")

            // Try to determine latest matching bundle version and platforms
            val bundleVersion = try {
                this.bundleRepository.queryLatestMatchingVersion(updateInfoRequest.bundleName, rVersion.version)
            } catch (e: NoSuchElementException) {
                log.warn(e.message)
                null
            }

            val bundleVersionPlatforms =
                    if (bundleVersion != null)
                        this.bundleRepository.listPlatforms(updateInfoRequest.bundleName, bundleVersion)
                    else ArrayList<String>()

            val versionPattern = rVersion.version

            replyChannel!!.send(UpdateInfo(
                    bundleName = updateInfoRequest.bundleName,
                    bundleVersionPattern = versionPattern,
                    // TODO: support for desired restart time
                    latestDesignatedVersion = bundleVersion?.toString(),
                    latestDesignatedVersionPlatforms = bundleVersionPlatforms.map { it.toString() }.toTypedArray()))

        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}