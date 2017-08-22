package org.deku.leoz.node.service.internal.filesync

import org.deku.leoz.identity.Identity
import sx.Disposable
import sx.mq.MqHandler
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
        MqHandler<FileSyncMessage>,
        Disposable {
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
     * @param identityUid Node identity
     */
    fun nodeInDirectory(identityUid: Identity.Uid): File {
        val d = File(this.inDirectory, identityUid.short)
        d.mkdirs()
        return d
    }

    /**
     * Builds transfer out directory for specific node and makes sure it exists
     * @param identityUid Node identity
     */
    fun nodeOutDirectory(identityUid: Identity.Uid): File {
        val d = File(this.outDirectory, identityUid.short)
        d.mkdirs()
        return d
    }

    fun nodeInRelativePath(identityUid: Identity.Uid = this.identity.uid): String {
        return "${this.inDirectoryName}/${identityUid.short}"
    }

    fun nodeOutRelativePath(identityUid: Identity.Uid = this.identity.uid): String {
        return "${this.outDirectoryName}/${identityUid.short}"
    }
}