package org.deku.leoz.central.services

import org.deku.leoz.Identity
import org.deku.leoz.config.messaging.MessagingConfiguration
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import org.deku.leoz.node.services.FileSyncServiceBase
import sx.jms.Channel
import java.io.File
import java.util.concurrent.ScheduledExecutorService

/**
 * Created by masc on 17/03/16.
 */
class FileSyncHostService(
        baseDirectory: File,
        executorService: ScheduledExecutorService,
        private val messagingConfiguration: MessagingConfiguration)
:
        FileSyncServiceBase(
                baseDirectory = baseDirectory,
                executorService = executorService
        ) {
    override fun run() {
        throw UnsupportedOperationException()
    }

    /**
     * FileSyncMessage handler
     */
    override fun onMessage(message: FileSyncMessage, replyChannel: Channel?) {
        val identityKey = Identity.Key(message.key)

        // Prepare directories
        val inDirectory = this.nodeInDirectory(identityKey)
        val outDirectory = this.nodeOutDirectory(identityKey)
    }
}