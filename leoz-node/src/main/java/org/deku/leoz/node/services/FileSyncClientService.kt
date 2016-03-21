package org.deku.leoz.node.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.node.messaging.entities.FileSyncMessage
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
     * Service implementation
     */
    override fun run() {
        try {
            if (this.outDirectory.listFiles().count() > 0) {
                this.ping()

                log.info("Synchronizing [${this.outDirectory}] -> [${this.rsyncEndpointIn.moduleUri}]")
                val rsyncClient = this.createRsyncClient(this.rsyncEndpointIn)
                rsyncClient.sync(
                        Rsync.URI(this.outDirectory),
                        this.rsyncEndpointIn.moduleUri,
                        { r ->
                            log.info("Synchronizing out [${r.path}]")
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
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    /**
     * Ping with FileSyncMessage
     */
    private fun ping() {
        this.centralChannelSupplier().use {
            it.sendRequest(FileSyncMessage(this.identity.key)).use {
                it.receive()
            }
        }
    }

    override fun onStart() {
        try {
            this.ping()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }

    override fun onMessage(message: FileSyncMessage, replyChannel: Channel?) {
        try {
            // TODO: perform sync of out/in in dedicated triggerable service to prevent concurrent notifications causing conflicts
            log.info("Received file sync notification")
            val rsyncClient = this.createRsyncClient(this.rsyncEndpointOut)
            rsyncClient.sync(
                    this.rsyncEndpointOut.moduleUri,
                    Rsync.URI(this.inDirectory),
                    { r ->
                        log.info("Synchronizing in [${r.path}]")
                    })
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}