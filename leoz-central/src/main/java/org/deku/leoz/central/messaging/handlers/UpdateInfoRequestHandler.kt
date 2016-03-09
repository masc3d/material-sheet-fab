package org.deku.leoz.central.messaging.handlers

import org.apache.commons.logging.LogFactory
import org.deku.leoz.bundle.entities.UpdateInfo
import org.deku.leoz.bundle.entities.UpdateInfoRequest
import org.deku.leoz.central.data.repositories.BundleVersionRepository
import org.deku.leoz.central.data.repositories.NodeRepository
import sx.jms.Channel
import sx.jms.Converter
import sx.jms.Handler
import javax.inject.Inject
import javax.inject.Named
import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.Session

/**
 * Update info service, providing version pattern information to clients
 * Created by masc on 19.10.15.
 */
@Named
class UpdateInfoRequestHandler
:
        Handler<UpdateInfoRequest>
{
    private val log = LogFactory.getLog(this.javaClass)

    @Inject
    private lateinit var nodeRepository: NodeRepository

    @Inject
    private lateinit var bundleVersionRepository: BundleVersionRepository

    override fun onMessage(message: UpdateInfoRequest, converter: Converter, jmsMessage: Message, session: Session, connectionFactory: ConnectionFactory) {
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

        try {
            Channel(
                    connectionFactory = connectionFactory,
                    sessionTransacted = false,
                    destination = jmsMessage.jmsReplyTo,
                    converter = converter).use {

                it.send(UpdateInfo(
                        updateInfoRequest.bundleName,
                        versionPattern))

            }
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}