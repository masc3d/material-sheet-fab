package org.deku.leoz.node.services

import org.apache.commons.logging.LogFactory
import org.deku.leoz.Identity
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import sx.jms.Channel
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import java.io.File
import java.util.*
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
        private val rsyncEndpoint: Rsync.Endpoint,
        private val centralChannelSupplier: () -> Channel)
:
        FileSyncServiceBase(
                executorService = executorService,
                baseDirectory = baseDirectory,
                identity = identity) {

    private val log = LogFactory.getLog(this.javaClass)
    private val tasks = ArrayList<Task>()

    init {
        this.addTask(FileSyncClientService.Task(
                sourcePath = this.outDirectory,
                rsyncEndpoint = this.rsyncEndpoint))
    }

    /**
     * File synchronization task
     * Created by masc on 07-Mar-16.
     * @param sourcePath Source path
     * @param destinationPath Destination path
     * @param rsyncModuleUri Destination rsync module
     * @param rsyncPassword Destination rsync password
     * @param sshTunnelProvider SSH tunnel provider for rsync connections
     */
    private class Task(
            val sourcePath: File,
            val rsyncEndpoint: Rsync.Endpoint)
    :
            Runnable {
        private val log = LogFactory.getLog(this.javaClass)

        /**
         * Rsync client factory helper
         */
        private fun createRsyncClient(): RsyncClient {
            val rc = RsyncClient()
            rc.password = this.rsyncEndpoint.password
            rc.compression = 2
            rc.preservePermissions = false
            rc.preserveExecutability = true
            rc.preserveGroup = false
            rc.preserveOwner = false
            // Remove tranferred files
            rc.removeSourceFiles = true
            // Do not remove destination files
            rc.delete = false
            rc.sshTunnelProvider = this.rsyncEndpoint.sshTunnelProvider
            return rc
        }

        override fun run() {
            try {
                if (!sourcePath.exists())
                    throw IllegalStateException("Source path does not exist [${sourcePath}]")

                if (sourcePath.listFiles().count() > 0) {
                    log.info("Synchronizing [${sourcePath}] -> [${rsyncEndpoint.moduleUri}]")
                    val rsyncClient = this.createRsyncClient()
                    rsyncClient.sync(
                            Rsync.URI(this.sourcePath),
                            rsyncEndpoint.moduleUri,
                            { r ->
                                log.info("Synchronizing [${r.path}]")
                            })

                    // Remove empty directories
                    sourcePath.walkBottomUp().forEach {
                        if (!it.equals(sourcePath) &&
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
    }

    private fun addTask(task: Task) {
        synchronized(this.tasks) {
            this.tasks.add(task)
        }
    }

    /**
     * Service implementation
     */
    override fun run() {
        // Copy tasks
        val tasks = ArrayList<Task>()
        synchronized(this.tasks) {
            tasks.addAll(this.tasks)
        }

        // Run tasks
        tasks.forEach {
            try {
                it.run()
            } catch(e: Exception) {
                log.error(e.message, e)
            }
        }
    }

    override fun onMessage(message: FileSyncMessage, replyChannel: Channel?) {
        throw UnsupportedOperationException()
    }
}