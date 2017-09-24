package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.model.AdditionalInfo
import sx.collections.chunked
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.activation.MimeType
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * File service V1
 * Created by masc on 25.08.2017.
 */
@Path("internal/v1/file")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "File service")
@ApiKey(false)
interface FileServiceV1 {

    /**
     * File fragment message
     */
    @Serializable(0xef27382c9e8b98)
    class FileFragmentMessage(
            /** Node uid of sender */
            var nodeUid: String? = null,
            /** Unique identifier of this file */
            var fileUid: UUID? = null,
            /** Mime type of this file */
            var mimeType: String? = null,
            /** Zero-based index of chunk/fragment */
            var index: Int? = null,
            /** Total amount of chunks/fragments of the file this fragment belongs to */
            var total: Int? = null,
            /** Total file size */
            var totalSize: Int? = null,
            /** Maximum chunk size */
            var chunkSize: Int? = null,
            /** Payload of this fragment. Size of the last part/fragment can be smaller than chunk size */
            var payload: ByteArray? = null
    ) {
        override fun toString(): String =
                "${this.javaClass.simpleName}(nodeUid=${nodeUid}, fileUid=${fileUid}, mime-type=${mimeType}, chunkSize=${chunkSize}, totalSize=${totalSize}, index=${index}, total=${total}, payload.size=${payload?.size})"
    }
}

/**
 * Creates file fragment messages from in-memory data
 */
fun ByteArray.toFileFragmentMessages(
        nodeUid: String,
        fileUid: UUID,
        mimeType: String,
        maxChunkSize: Int
): List<FileServiceV1.FileFragmentMessage> {
    val chunks = this.chunked(maxChunkSize)

    return chunks.mapIndexed { index, bytes ->
        FileServiceV1.FileFragmentMessage(
                nodeUid = nodeUid,
                fileUid = fileUid,
                mimeType = mimeType,
                index = index,
                total = chunks.size,
                chunkSize = maxChunkSize,
                totalSize = this.size,
                payload = bytes)
    }
}
