package org.deku.leoz.node.service.internal.filesync

import org.deku.leoz.identity.Identity
import java.io.File
import java.util.concurrent.ScheduledExecutorService

/**
 * Base class for file sync host and client service
 * Created by masc on 20/03/16.
 */
abstract class FileSyncServiceBase(
        protected val executorService: ScheduledExecutorService,
        val baseDirectory: File,
        val identity: Identity)
:
        sx.jms.Handler<FileSyncMessage>,
        sx.Disposable {
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

    fun nodeInRelativePath(identityKey: Identity.Key = this.identity.key): String {
        return "${this.inDirectoryName}/${identityKey.short}"
    }

    fun nodeOutRelativePath(identityKey: Identity.Key = this.identity.key): String {
        return "${this.outDirectoryName}/${identityKey.short}"
    }
}