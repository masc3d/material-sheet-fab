package org.deku.leoz.node.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import sx.concurrent.Service
import sx.jms.Channel
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.util.concurrent.ScheduledExecutorService

/**
 * File sync client service
 * @param executorService Executor service
 * @param baseDirectory Base directory
 * @param rsyncEndpoint Rsync end point
 * Created by masc on 11/03/16.
 */
class FileSyncClientService constructor(
        executorService: ScheduledExecutorService,
        baseDirectory: File,
        identity: Identity,
        rsyncEndpoint: Rsync.Endpoint,
        private val centralChannelSupplier: () -> Channel)
:
        FileSyncServiceBase(
                executorService = executorService,
                baseDirectory = baseDirectory,
                identity = identity) {

    private val log = LogFactory.getLog(this.javaClass)
    private val rsyncEndpointOut: Rsync.Endpoint
    private val rsyncEndpointIn: Rsync.Endpoint

    init {
        this.rsyncEndpointOut = Rsync.Endpoint(
                moduleUri = rsyncEndpoint.moduleUri.resolve(this.nodeOutRelativePath()),
                password = rsyncEndpoint.password,
                sshTunnelProvider = rsyncEndpoint.sshTunnelProvider)

        this.rsyncEndpointIn = Rsync.Endpoint(
                moduleUri = rsyncEndpoint.moduleUri.resolve(this.nodeInRelativePath()),
                password = rsyncEndpoint.password,
                sshTunnelProvider = rsyncEndpoint.sshTunnelProvider)
    }

    private val incomingSyncService = object : Service(this.executorService) {
        override fun run() {
            try {
                this@FileSyncClientService.syncIncoming()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    /**
     * Rsync client factory helper
     */
    private fun createRsyncClient(rsyncEndpoint: Rsync.Endpoint): RsyncClient {
        val rc = RsyncClient()
        rc.password = rsyncEndpoint.password
        rc.compression = 2
        rc.preservePermissions = false
        rc.preserveExecutability = true
        rc.preserveGroup = false
        rc.preserveOwner = false
        // Remove tranferred files
        rc.removeSourceFiles = true
        // Do not remove destination files
        rc.delete = false
        rc.sshTunnelProvider = rsyncEndpoint.sshTunnelProvider
        return rc
    }

    /**
     * Synchronize outgoing files with host
     */
    private fun syncOutgoing() {
        if (this.outDirectory.listFiles().count() > 0) {
            this.ping()

            log.info("Synchronizing [${this.outDirectory}] -> [${this.rsyncEndpointIn.moduleUri}]")
            val rsyncClient = this.createRsyncClient(this.rsyncEndpointIn)
            rsyncClient.sync(
                    Rsync.URI(this.outDirectory),
                    this.rsyncEndpointIn.moduleUri,
                    { r ->
                        log.debug("out [${r.path}]")
                    })

            // Remove empty directories
            this.outDirectory.walkBottomUp().forEach {
                if (!it.equals(this.outDirectory) &&
                        it.isDirectory &&
                        it.listFiles().count() == 0) {
                    it.delete()
                }
            }
        }
    }

    /**
     * Synchronize incoming files with host
     */
    private fun syncIncoming() {
        log.info("Synchronizing [${this.rsyncEndpointOut.moduleUri}] -> [${this.inDirectory}]")
        val rsyncClient = this.createRsyncClient(this.rsyncEndpointOut)
        rsyncClient.sync(
                this.rsyncEndpointOut.moduleUri,
                Rsync.URI(this.inDirectory),
                { r ->
                    log.debug("in [${r.path}]")
                })
    }

    /**
     * Ping host (with message)
     */
    private fun ping() {
        this.centralChannelSupplier().use {
            it.sendRequest(FileSyncMessage(this.identity.key)).use {
                it.receive()
            }
        }
    }

    /**
     * Service implementation
     */
    override fun run() {
        try {
            this.syncOutgoing()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    /**
     * On service start
     */
    override fun onStart() {
        this.incomingSyncService.start()

        try {
            this.ping()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    override fun onStop(interrupted: Boolean) {
        this.incomingSyncService.stop(interrupt = interrupted)
    }

    /**
     * On file sync message
     */
    override fun onMessage(message: FileSyncMessage, replyChannel: Channel?) {
        log.info("Received notification, files available")
        this.incomingSyncService.trigger()
    }
}