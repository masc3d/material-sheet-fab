package org.deku.leoz.node.service.internal

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.tika.mime.MimeTypes
import org.deku.leoz.node.Storage
import org.deku.leoz.service.internal.FileServiceV1
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sx.io.serialization.JacksonSerializer
import sx.io.serialization.Serializable
import sx.io.serialization.Serializer
import sx.log.slf4j.trace
import sx.mq.MqChannel
import sx.mq.MqHandler
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import javax.inject.Inject
import javax.ws.rs.Path

/**
 * File service V1 implementation
 * Created by masc on 25.08.17.
 */
@Component
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

    init {
        Serializer.types.register(FileMeta::class.java)
    }

    @Inject
    private lateinit var storage: Storage

    private val objectMapper by lazy { ObjectMapper() }

    override fun onMessage(message: FileServiceV1.FileFragmentMessage, replyChannel: MqChannel?) {
        log.trace { "Received ${message}" }

        // Extract and verify message parameters

        message.nodeUid ?:
                throw IllegalArgumentException("Node UID must not be null")

        val fileUid = message.fileUid ?:
                throw IllegalArgumentException("File UID must not be null")

        val mimeType = MimeTypes.getDefaultMimeTypes().forName(message.mimeType ?:
                throw IllegalArgumentException("Mime type must not be null")
        )

        val payload = message.payload ?:
                throw IllegalArgumentException("Payload must not be null")

        val index = message.index ?:
                throw IllegalArgumentException("Index must not be null")

        val total = message.total ?:
                throw IllegalArgumentException("File UID must not be null")

        // Arguments which are optional for backward compatibility
        val chunkSize = message.chunkSize
        val totalSize = message.totalSize

        // File names/extensions

        val extension = mimeType.extension
        val filename = "${fileUid}${extension}"
        val metaFilename = "${fileUid}${extension}.meta"
        val tempFilename = "${fileUid}${extension}.temp"

        // Files

        val file = File(storage.workTmpDataDirectory, filename)
        val metaFile = File(storage.workTmpDataDirectory, metaFilename)
        val tempFile = File(storage.workTmpDataDirectory, tempFilename)

        var isFileComplete = false

        FileChannel.open(
                metaFile.toPath(),
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE).use { metaFileChannel ->

            // Lock against meta file to synchronize access
            metaFileChannel.lock().use {
                val serializer = JacksonSerializer(this.objectMapper)

                val meta = if (metaFileChannel.size() > 0) {
                    // Deserialize meta data
                    val buffer = ByteBuffer.allocate(metaFileChannel.size().toInt())
                    metaFileChannel.read(buffer, 0)
                    serializer.deserializeFrom(buffer.array()) as FileMeta
                } else {
                    // Create new meta structure
                    FileMeta(total = total)
                }

                val isNewFile = tempFile.exists()

                when {
                    chunkSize != null && totalSize != null -> {
                        // Use random access file, allowing creation of fixed size file and writing to specific positions
                        RandomAccessFile(tempFile, "rw").use { tempRaFile ->
                            if (isNewFile) {
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
                                metaFileChannel.truncate(0)
                                metaFileChannel.write(ByteBuffer.wrap(serializer.serializeToByteArray(meta)))
                            }
                        }
                    }
                    else -> {
                        // Legacy support for older clients, which are not providing chunkSize & totalSize
                        FileOutputStream(tempFile, true).use { stream ->
                            stream.write(payload)
                        }

                        isFileComplete = index == (total - 1)
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