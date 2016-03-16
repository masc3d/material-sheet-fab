package org.deku.leoz.node.services

import org.apache.commons.logging.LogFactory
import sx.concurrent.Service
import sx.rsync.Rsync
import sx.rsync.RsyncClient
import sx.ssh.SshTunnelProvider
import java.io.File
import java.time.Duration
import java.util.*
import java.util.concurrent.ScheduledExecutorService

/**
 * Created by masc on 11/03/16.
 */
class FileSyncClientService constructor(
        executorService: ScheduledExecutorService)
:
        Service(executorService = executorService,
                period = Duration.ofSeconds(10)) {
    private val log = LogFactory.getLog(this.javaClass)
    private val tasks = ArrayList<Task>()

    /**
     * File synchronization task
     * Created by masc on 07-Mar-16.
     * @param sourcePath Source path
     * @param destinationPath Destination path
     * @param rsyncModuleUri Destination rsync module
     * @param rsyncPassword Destination rsync password
     * @param sshTunnelProvider SSH tunnel provider for rsync connections
     */
    class Task(
            val sourcePath: File,
            val rsyncModuleUri: Rsync.URI,
            val rsyncPassword: String = "",
            val sshTunnelProvider: SshTunnelProvider? = null)
    :
            Runnable {
        private val log = LogFactory.getLog(this.javaClass)

        /**
         * Rsync client factory helper
         */
        private fun createRsyncClient(): RsyncClient {
            val rc = RsyncClient()
            rc.password = this.rsyncPassword
            rc.compression = 2
            rc.preservePermissions = false
            rc.preserveExecutability = true
            rc.preserveGroup = false
            rc.preserveOwner = false
            // Remove tranferred files
            rc.removeSourceFiles = true
            // Do not remove destination files
            rc.delete = false
            rc.sshTunnelProvider = this.sshTunnelProvider
            return rc
        }

        override fun run() {
            try {
                if (!sourcePath.exists())
                    throw IllegalStateException("Source path does not exist [${sourcePath}]")

                if (sourcePath.listFiles().count() > 0) {
                    log.info("Synchronizing [${sourcePath}] -> [${rsyncModuleUri}]")
                    val rsyncClient = this.createRsyncClient()
                    rsyncClient.sync(
                            Rsync.URI(this.sourcePath),
                            rsyncModuleUri,
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

    fun addTask(task: Task) {
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
}