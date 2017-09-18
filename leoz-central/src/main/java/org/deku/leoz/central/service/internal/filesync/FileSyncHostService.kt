package org.deku.leoz.central.service.internal.filesync

import org.deku.leoz.identity.Identity
import org.deku.leoz.io.*
import java.io.File
import org.deku.leoz.node.service.internal.filesync.*
import org.threeten.bp.Duration
import sx.mq.MqChannel
import sx.mq.jms.JmsEndpoint
import sx.mq.jms.channel
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * File sync host service
 * Created by masc on 17/03/16.
 */
class FileSyncHostService(
        executorService: ScheduledExecutorService,
        baseDirectory: File,
        identity: Identity,
        private val nodeEndpointSupplier: (identityUid: Identity.Uid) -> JmsEndpoint)
:
        FileSyncServiceBase(
                executorService = executorService,
                baseDirectory = baseDirectory,
                identity = identity) {
    /**
     * Node metadata contianer
     */
    class Node(
            val identityUid: Identity.Uid,
            val outDirectory: File) {
    }

    private val log = org.slf4j.LoggerFactory.getLogger(this.javaClass)
    /** Known nodes */
    private val nodes = java.util.HashMap<Identity.Uid, Node>()
    /** Known nodes by watchkey */
    private val nodesByWatchkey = HashMap<WatchKey, Node>()
    /** Reentrant lock */
    private val lock = ReentrantLock()
    /** Filesystem watch service */
    private val watchService: WatchService = WatchServiceFactory.newWatchService()

    private final val WATCHSERVICE_INTERVAL = Duration.ofSeconds(1)

    /** Watch maintenance and notification service */
    private val service = object : sx.concurrent.Service(
            executorService = executorService,
            initialDelay = Duration.ZERO) {

        /**
         * Service logic
         */
        override fun run() {
            try {
                while (this.isStarted) {
                    val wks = java.util.HashSet<WatchKey>()

                    try {
                        wks.clear()
                        var wk: WatchKey?

                        // Wait for next and poll all watchkeys
                        wk = watchService.take()
                        while (wk != null) {
                            wks.add(wk)
                            wk = watchService.poll()
                        }

                        // Map watchkeys to node metadata
                        val nodes = lock.withLock {
                            wks.mapNotNull {
                                nodesByWatchkey.get(it) ?: run { log.warn("Unknown watch key [${it}]"); null }
                            }
                        }

                        // Poll all events of watchkeys and reset
                        wks.forEach {
                            it.pollEvents()
                            it.reset()
                        }

                        // Notify nodes
                        nodes.forEach {
                            this@FileSyncHostService.notifyNode(it.identityUid)
                        }
                    } catch(e: InterruptedException) {
                        throw e
                    } catch(e: Exception) {
                        log.error(e.message, e)
                    }

                    // Wait short while for more events to arrive
                    Thread.sleep(WATCHSERVICE_INTERVAL.toMillis())
                }
            } catch(e: InterruptedException) {
            } catch(e: Exception) {
                log.error(e.message, e)
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
    override fun onMessage(message: FileSyncMessage, replyChannel: MqChannel?) {
        try {
            val identityUid = Identity.Uid(message.uid)
            log.info("Received ping from [${identityUid}]")

            // Prepare directories
            this.nodeInDirectory(identityUid)
            val nodeOutDirectory = this.nodeOutDirectory(identityUid)

            lock.withLock {
                this.nodes.getOrPut(identityUid, {
                    val node = FileSyncHostService.Node(
                            identityUid = identityUid,
                            outDirectory = nodeOutDirectory)

                    val watchable = WatchServiceFactory.newWatchable(nodeOutDirectory, this.watchService)
                    val wk = watchable.register(this.watchService,
                            arrayOf(StandardWatchEventKinds.ENTRY_MODIFY,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.OVERFLOW))

                    this.nodesByWatchkey.put(wk, node)

                    this@FileSyncHostService.notifyNode(identityUid)
                    node
                })
            }

            // Send back empty file sync message as a confirmation
            if (replyChannel != null) {
                replyChannel.send(FileSyncMessage())
            }

            this.notifyNode(identityUid)
        } catch(e: Exception) {
            this.log.error(e.message, e)
        }
    }

    /**
     * Notify node about available files
     * @param identityUid Node identity key
     */
    private fun notifyNode(identityUid: Identity.Uid) {
        val out = this.nodeOutDirectory(identityUid)
        if (out.exists() && out.listFiles().count() > 0) {
            log.trace("Sending file sync notification to [${identityUid}]")
            this.nodeEndpointSupplier(identityUid).channel().use {
                it.send(FileSyncMessage())
            }
        }
    }
}