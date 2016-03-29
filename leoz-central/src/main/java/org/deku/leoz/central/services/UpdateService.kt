package org.deku.leoz.central.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.BundleVersionRepository
import org.deku.leoz.central.data.repositories.NodeRepository
import sx.jms.Channel
import sx.jms.Handler
import java.util.*

/**
 * Update info service, providing version pattern information to clients
 * Created by masc on 19.10.15.
 */
class UpdateService(
        private val nodeRepository: NodeRepository,
        private val bundleVersionRepository: BundleVersionRepository,
        private val localBundleRepository: BundleRepository
)
:
        Handler<UpdateInfoRequest> {
    private val log = LogFactory.getLog(this.javaClass)

    override fun onMessage(message: UpdateInfoRequest, replyChannel: Channel?) {
        try {
            val updateInfoRequest = message

            val rNode = nodeRepository.findByKey(message.nodeKey)
            if (rNode == null)
                throw IllegalArgumentException("Unknown node [${message.nodeKey}}")

            val versionAlias = message.versionAlias ?: rNode.versionAlias ?: "release"
            val rVersion = bundleVersionRepository.findByAlias(
                    bundleName = updateInfoRequest.bundleName,
                    versionAlias = versionAlias)

            if (rVersion == null)
                throw IllegalArgumentException("No version recxord for node [${updateInfoRequest.nodeKey}] bundle [${updateInfoRequest.bundleName}] version alias [${versionAlias}]")

            // Try to determine latest matching bundle version and platforms
            val bundleVersion = try {
                this.localBundleRepository.queryLatestMatchingVersion(updateInfoRequest.bundleName, rVersion.version)
            } catch (e: NoSuchElementException) {
                log.warn(e.message)
                null
            }

            val bundleVersionPlatforms =
                    if (bundleVersion != null)
                        this.localBundleRepository.listPlatforms(updateInfoRequest.bundleName, bundleVersion)
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