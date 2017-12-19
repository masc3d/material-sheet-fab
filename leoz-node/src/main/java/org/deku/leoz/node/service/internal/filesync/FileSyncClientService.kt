package org.deku.leoz.node.service.internal.filesync

import org.deku.leoz.identity.Identity
import org.threeten.bp.Duration
import sx.concurrent.Service
import sx.log.slf4j.debug
import sx.mq.MqChannel
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.channel
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.nio.file.StandardWatchEventKinds
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
        private val centralEndpointSupplier: () -> JmsEndpoint)
    :
        FileSyncServiceBase(
                executorService = executorService,
                baseDirectory = baseDirectory,
                identity = identity),
        sx.Lifecycle {
    private final val RETRY_INTERVAL = Duration.ofSeconds(3)

    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)
    private val rsyncEndpointOut: Rsync.Endpoint
    private val rsyncEndpointIn: Rsync.Endpoint
    private val watchService: java.nio.file.WatchService

    private final val WATCHSERVICE_INTERVAL = Duration.ofSeconds(1)

    init {
        this.rsyncEndpointOut = Rsync.Endpoint(
                moduleUri = rsyncEndpoint.moduleUri.resolve(this.nodeOutRelativePath()),
                password = rsyncEndpoint.password,
                sshTunnelProvider = rsyncEndpoint.sshTunnelProvider)

        this.rsyncEndpointIn = Rsync.Endpoint(
                moduleUri = rsyncEndpoint.moduleUri.resolve(this.nodeInRelativePath()),
                password = rsyncEndpoint.password,
                sshTunnelProvider = rsyncEndpoint.sshTunnelProvider)

        this.watchService = org.deku.leoz.io.WatchServiceFactory.newWatchService()
    }

    private val incomingSyncService = object : Service(this.executorService) {
        override fun run() {
            var success = false
            while (!success) {
                try {
                    this@FileSyncClientService.syncIncoming()
                    success = true
                } catch(e: InterruptedException) {
                    throw e
                } catch(e: Exception) {
                    log.error(e.message, e)
                    log.info("Retrying download in ${RETRY_INTERVAL}")
                    Thread.sleep(RETRY_INTERVAL.toMillis())
                }
            }
        }
    }

    private val outgoingSyncService = object : Service(this.executorService, initialDelay = Duration.ZERO) {
        override fun run() {
            var wk: java.nio.file.WatchKey? = null
            try {
                wk = org.deku.leoz.io.WatchServiceFactory.newWatchable(file = this@FileSyncClientService.outDirectory, watchService = this@FileSyncClientService.watchService)
                        .register(this@FileSyncClientService.watchService,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.OVERFLOW)

                this@FileSyncClientService.syncOutgoing(pingAlways = true)

                while (this.isStarted) {
                    this@FileSyncClientService.watchService.take()

                    wk.pollEvents()
                    wk.reset()

                    var success = false
                    while (!success) {
                        try {
                            this@FileSyncClientService.syncOutgoing()
                            success = true
                        } catch(e: InterruptedException) {
                            throw e
                        } catch(e: Exception) {
                            log.error(e.message, e)
                            log.info("Retrying upload in ${RETRY_INTERVAL}")
                            Thread.sleep(RETRY_INTERVAL.toMillis())
                        }
                    }

                    // Wait for a short while for more events to arrive.
                    Thread.sleep(WATCHSERVICE_INTERVAL.toMillis())
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
        // Performance optimizations
        // Disable checksum check
        rc.skipBasedOnChecksum = false
        // Disable delta transfer
        rc.wholeFile = true
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
            val sw = com.google.common.base.Stopwatch.createStarted()
            log.info("Uploading [${this.outDirectory}] -> [${this.rsyncEndpointIn.moduleUri}]")
            val rsyncClient = this.createRsyncClient(this.rsyncEndpointIn)
            rsyncClient.sync(
                    Rsync.URI(this.outDirectory),
                    this.rsyncEndpointIn.moduleUri,
                    { r ->
                        log.debug { "out [${r.path}]" }
                    })

            // Remove empty directories
            this.outDirectory.walkBottomUp().forEach {
                if (it != this.outDirectory &&
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
        val sw = com.google.common.base.Stopwatch.createStarted()
        log.info("Downloading [${this.rsyncEndpointOut.moduleUri}] -> [${this.inDirectory}]")
        val rsyncClient = this.createRsyncClient(this.rsyncEndpointOut)
        rsyncClient.sync(
                this.rsyncEndpointOut.moduleUri,
                Rsync.URI(this.inDirectory),
                { r ->
                    log.debug { "in [${r.path}]" }
                })
        log.info("Download complete [${sw}]")
    }

    /**
     * Ping host (with message)
     */
    private fun ping() {
        this.centralEndpointSupplier().channel().use() {
            it.sendRequest(FileSyncMessage(this.identity.uid.value)).use {
                it.receive()
            }
        }
    }

    override fun start() {
        this.outgoingSyncService.start()
        this.incomingSyncService.start()

    }

    override fun stop() {
        this.outgoingSyncService.stop()
        this.incomingSyncService.stop()
    }

    override fun restart() {
        this.outgoingSyncService.restart()
        this.incomingSyncService.restart()
    }

    override fun isRunning(): Boolean {
        return this.incomingSyncService.isRunning()
    }

    override fun close() {
        this.stop()
    }

    /**
     * On file sync message
     */
    override fun onMessage(message: FileSyncMessage, replyChannel: MqChannel?) {
        log.info("Received notification, files available for download")
        try {
            this.incomingSyncService.trigger()
        } catch(e: Exception) {
            log.error(e.message, e)
        }
    }
}