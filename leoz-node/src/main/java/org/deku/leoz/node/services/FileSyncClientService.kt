package org.deku.leoz.node.services

import com.google.common.base.Stopwatch
import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.io.WatchServiceFactory
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import sx.concurrent.Service
import sx.jms.Channel
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.time.Duration
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
    private val watchService: WatchService

    init {
        this.rsyncEndpointOut = Rsync.Endpoint(
                moduleUri = rsyncEndpoint.moduleUri.resolve(this.nodeOutRelativePath()),
                password = rsyncEndpoint.password,
                sshTunnelProvider = rsyncEndpoint.sshTunnelProvider)

        this.rsyncEndpointIn = Rsync.Endpoint(
                moduleUri = rsyncEndpoint.moduleUri.resolve(this.nodeInRelativePath()),
                password = rsyncEndpoint.password,
                sshTunnelProvider = rsyncEndpoint.sshTunnelProvider)

        this.watchService = WatchServiceFactory.newWatchService()
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

    private val outgoingSyncService = object : Service(this.executorService, initialDelay = Duration.ZERO) {
        override fun run() {
            var wk: WatchKey? = null
            try {
                wk = WatchServiceFactory.newWatchable(file = this@FileSyncClientService.outDirectory, watchService = this@FileSyncClientService.watchService)
                        .register(this@FileSyncClientService.watchService,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.OVERFLOW)

                this@FileSyncClientService.syncOutgoing(pingAlways = true)

                while (this.isStarted) {
                    this@FileSyncClientService.watchService.take()
                    log.info("Detected files available for upload")
                    // Wait for a short while for more events to arrive.
                    Thread.sleep(100)
                    wk.pollEvents()
                    wk.reset()
                    try {
                        this@FileSyncClientService.syncOutgoing()
                    } catch(e: InterruptedException) {
                        throw e
                    } catch(e: Exception) {
                        log.error(e.message, e)
                    }
                }
            } catch(e: InterruptedException) {
            } catch(e: Exception) {
                log.error(e.message, e)
            } finally {
                wk?.cancel()
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
    private fun syncOutgoing(pingAlways: Boolean = false) {
        val hasFiles = this.outDirectory.listFiles().count() > 0

        if (hasFiles || pingAlways)
            this.ping()

        if (hasFiles) {
            val sw = Stopwatch.createStarted()
            log.info("Uploading [${this.outDirectory}] -> [${this.rsyncEndpointIn.moduleUri}]")
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
            log.info("Upload complete [${sw}]")
        }
    }

    /**
     * Synchronize incoming files with host
     */
    private fun syncIncoming() {
        val sw = Stopwatch.createStarted()
        log.info("Downloading [${this.rsyncEndpointOut.moduleUri}] -> [${this.inDirectory}]")
        val rsyncClient = this.createRsyncClient(this.rsyncEndpointOut)
        rsyncClient.sync(
                this.rsyncEndpointOut.moduleUri,
                Rsync.URI(this.inDirectory),
                { r ->
                    log.debug("in [${r.path}]")
                })
        log.info("Download complete [${sw}]")
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

    fun start() {
        this.outgoingSyncService.start()
        this.incomingSyncService.start()

    }

    fun stop() {
        this.outgoingSyncService.stop()
        this.incomingSyncService.stop()
    }

    fun restart() {
        this.outgoingSyncService.restart()
        this.incomingSyncService.restart()
    }

    override fun close() {
        this.stop()
    }

    /**
     * On file sync message
     */
    override fun onMessage(message: FileSyncMessage, replyChannel: Channel?) {
        log.info("Received notification, files available for download")
        try {
            this.incomingSyncService.trigger()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}