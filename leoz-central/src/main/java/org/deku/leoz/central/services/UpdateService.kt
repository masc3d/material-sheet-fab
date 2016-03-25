package org.deku.leoz.central.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.BundleRepository
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.BundleVersionRepository
import org.deku.leoz.central.data.repositories.NodeRepository
import sx.jms.Channel
import sx.jms.Handler

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
        Handler<UpdateInfoRequest>
{
    private val log = LogFactory.getLog(this.javaClass)

    override fun onMessage(message: UpdateInfoRequest, replyChannel: Channel?) {
        try {
            val updateInfoRequest = message

            val rNode = nodeRepository.findByKey(message.nodeKey)
            if (rNode == null)
                throw IllegalArgumentException("Unknown node [${message.nodeKey}}")

            val versionAlias = rNode.versionAlias ?: "release"
            val rVersion = bundleVersionRepository.findByAlias(
                    bundleName = updateInfoRequest.bundleName,
                    versionAlias = versionAlias)

            if (rVersion == null)
                throw IllegalArgumentException("No version recxord for node [${updateInfoRequest.nodeKey}] bundle [${updateInfoRequest.bundleName}] version alias [${versionAlias}]")

            val versionPattern = rVersion.version

            replyChannel!!.send(UpdateInfo(
                    updateInfoRequest.bundleName,
                    versionPattern))

        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}