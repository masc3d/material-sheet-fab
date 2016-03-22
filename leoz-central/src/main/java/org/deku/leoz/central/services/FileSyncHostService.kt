package org.deku.leoz.central.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import org.deku.leoz.node.services.FileSyncServiceBase
import sx.concurrent.Service
import sx.jms.Channel
import java.io.File
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService

/**
 * Created by masc on 17/03/16.
 */
class FileSyncHostService(
        executorService: ScheduledExecutorService,
        baseDirectory: File,
        identity: Identity,
        private val nodeChannelSupplier: (identityKey: Identity.Key) -> Channel)
:
        FileSyncServiceBase(
                executorService = executorService,
                baseDirectory = baseDirectory,
                identity = identity) {
    class Node(val identityKey: Identity.Key) {
    }

    val log = LogFactory.getLog(this.javaClass)
    /** Known nodes */
    val nodes = ConcurrentHashMap<Identity.Key, Node>()

    val service = object : Service(executorService = executorService,
            period = Duration.ofSeconds(10)) {
        override fun run() {
            // All known nodes
            val identityKeys = this@FileSyncHostService.nodes.keys.toList()

            identityKeys.forEach {
                this@FileSyncHostService.notifyNode(it)
            }
        }
    }

    fun start() {
        this.service.start()
    }

    fun stop() {
        this.service.stop()
    }

    fun restart() {
        this.service.restart()
    }

    override fun close() {
        this.stop()
    }

    /**
     * FileSyncMessage handler
     */
    override fun onMessage(message: FileSyncMessage, replyChannel: Channel?) {
        try {
            val identityKey = Identity.Key(message.key)

            val node: Node = this.nodes.getOrPut(identityKey, { Node(identityKey) })

            // Prepare directories
            this.nodeInDirectory(identityKey)
            this.nodeOutDirectory(identityKey)

            // Send back empty file sync message as a confirmation
            if (replyChannel != null) {
                replyChannel.send(FileSyncMessage())
            }

            this.notifyNode(identityKey)
        } catch(e: Exception) {
            this.log.error(e.message, e)
        }

    }

    /**
     * Notify node about available files
     * @param identityKey Node identity key
     */
    private fun notifyNode(identityKey: Identity.Key) {
        val out = this.nodeOutDirectory(identityKey)
        if (out.exists() && out.listFiles().count() > 0) {
            log.info("Outgoing files available for node [${identityKey}]")
            this.nodeChannelSupplier(identityKey).use {
                it.send(FileSyncMessage())
            }
        }
    }
}