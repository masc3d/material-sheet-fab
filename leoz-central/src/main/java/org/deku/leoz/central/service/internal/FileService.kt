package org.deku.leoz.central.service.internal

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.MediaType
import kotlinx.support.jdk7.use
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.FileServiceV1
import org.slf4j.LoggerFactory
import sx.io.serialization.JacksonSerializer
import sx.io.serialization.Serializable
import sx.mq.MqChannel
import sx.mq.MqHandler
import sx.rs.auth.ApiKey
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
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

    @Serializable(0x5880838e3ce330)
    private data class FileMeta(
            var total: Int = 0,
            var parts: Array<Int> = arrayOf()
    )

    @Inject
    private lateinit var storage: Storage

    private val objectMapper by lazy { ObjectMapper() }

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

        val chunkSize = message.chunkSize ?:
                throw IllegalArgumentException("Chunk size must not be null")

        val totalSize = message.totalSize ?:
                throw IllegalArgumentException("Total size must not be null")

        // File names/extensions

        val extension = mimeType.subtype()
        val filename = "${fileUid}.${extension}"
        val metaFilename = "${fileUid}.${extension}.meta"
        val tempFilename = "${fileUid}.${extension}.temp"

        // Files

        val file = File(storage.workTmpDataDirectory, filename)
        val metaFile = File(storage.workTmpDataDirectory, metaFilename)
        val tempFile = File(storage.workTmpDataDirectory, tempFilename)

        var isFileComplete = false

        FileChannel.open(
                metaFile.toPath(),
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE).use { metaFileChannel ->

            // Lock against meta to synchronize access
            metaFileChannel.lock().use {
                val serializer = JacksonSerializer(this.objectMapper)

                val meta = if (metaFileChannel.size() > 0) {
                    // Deserialize meta data
                    metaFile.inputStream().use { stream ->
                        serializer.deserialize(stream) as FileMeta
                    }
                } else {
                    // Create new meta structure
                    FileMeta(total = total)
                }

                val newFile = tempFile.exists()

                // Use random access file, allowing creation of fixed size file and writing to specific positions
                RandomAccessFile(tempFile, "rw").use { tempRaFile ->
                    if (newFile) {
                        // Create file with fixed total size initially
                        tempRaFile.setLength(totalSize.toLong())
                    }

                    // Write chunk to file
                    tempRaFile.channel.use { tempFileChannel ->
                        tempFileChannel.position(index.toLong() * chunkSize)
                        tempFileChannel.write(ByteBuffer.wrap(payload))
                    }

                    // Update meta
                    meta.parts = meta.parts.plus(index)

                    isFileComplete = meta.total == meta.parts.size

                    // Serialize meta back to file (if necessary)
                    if (!isFileComplete) {
                        metaFile.outputStream().use { stream ->
                            serializer.serialize(stream, meta)
                        }
                    }
                }
            }
        }

        if (isFileComplete) {
            tempFile.renameTo(file)
            metaFile.delete()
        }
    }
}