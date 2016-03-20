package org.deku.leoz.node.services

import org.deku.leoz.Identity
import org.deku.leoz.node.messaging.entities.FileSyncMessage
import sx.concurrent.Service
import sx.jms.Handler
import java.io.File
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService

/**
 * Base class for file sync host and client service
 * Created by masc on 20/03/16.
 */
abstract class FileSyncServiceBase(
        val baseDirectory: File,
        executorService: ScheduledExecutorService)
:
        Service(executorService = executorService,
                period = Duration.ofSeconds(10)),
        Handler<FileSyncMessage> {
    val inDirectoryName = "in"
    val outDirectoryName = "out"

    val inDirectory by lazy {
        val d = File(this.baseDirectory, this.inDirectoryName)
        d.mkdirs()
        d
    }

    val outDirectory by lazy {
        val d = File(this.baseDirectory, this.outDirectoryName)
        d.mkdirs()
        d
    }

    /**
     * Build transfer in directory for specific node and makes sure it exists
     * @param identityKey Node identity
     */
    fun nodeInDirectory(identityKey: Identity.Key): File {
        val d = File(this.inDirectory, identityKey.short)
        d.mkdirs()
        return d
    }

    /**
     * Builds transfer out directory for specific node and makes sure it exists
     * @param identityKey Node identity
     */
    fun nodeOutDirectory(identityKey: Identity.Key): File {
        val d = File(this.outDirectory, identityKey.short)
        d.mkdirs()
        return d
    }

    fun nodeInRelativePath(identityKey: Identity.Key): String {
        return "${this.inDirectoryName}/${identityKey.short}"
    }

    fun nodeOutRelativePath(identityKey: Identity.Key): String {
        return "${this.outDirectoryName}/${identityKey.short}"
    }
}