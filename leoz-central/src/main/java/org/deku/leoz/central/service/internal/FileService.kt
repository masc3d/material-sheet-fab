package org.deku.leoz.central.service.internal

import com.google.common.net.MediaType
import org.deku.leoz.central.config.PersistenceConfiguration
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.ParcelServiceV1
import org.deku.leoz.service.internal.FileServiceV1
import org.deku.leoz.service.internal.isLastFragment
import org.slf4j.LoggerFactory
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Path

/**
 * File service V1 implementation
 * Created by masc on 25.08.17.
 */
@Named
@ApiKey(false)
@Path("internal/v1/file")
class FileServiceV1 :
        FileServiceV1,
        MqHandler<FileServiceV1.FileFragmentMessage> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Inject
    private lateinit var storage: Storage

    override fun onMessage(message: FileServiceV1.FileFragmentMessage, replyChannel: MqChannel?) {
        log.trace("Received ${message}")

        // Extract and verify message parameters

        val nodeUid = message.nodeUid ?:
                throw IllegalArgumentException("Node UID must not be null")

        val fileUid = message.fileUid ?:
                throw IllegalArgumentException("File UID must not be null")

        val mimeType = MediaType.parse(message.mimeType ?:
                throw IllegalArgumentException("Mime type must not be null")
        )

        val payload = message.payload ?:
                throw IllegalArgumentException("Payload must not be null")

        val index = message.index ?:
                throw IllegalArgumentException("Index must not be null")

        val total = message.total ?:
                throw IllegalArgumentException("File UID must not be null")

        // File related vars

        val extension = mimeType.subtype()
        val filename = "${fileUid}.${extension}"
        val tempFilename = "${fileUid}.${extension}.temp"

        val file = File(storage.workTmpDataDirectory, filename)
        val tempFile = File(storage.workTmpDataDirectory, tempFilename)

        FileOutputStream(tempFile, true).use {
            it.write(payload)
        }

        if (message.isLastFragment) {
            // Finalize file
            tempFile.renameTo(file)
        }
    }
}
