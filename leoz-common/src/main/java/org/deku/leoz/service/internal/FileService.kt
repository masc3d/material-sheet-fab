package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.model.AdditionalInfo
import sx.collections.chunked
import sx.io.serialization.Serializable
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
interface FileServiceV1 {

    /**
     * File fragment message
     */
    @Serializable(0xef27382c9e8b98)
    class FileFragmentMessage(
            /** Node uid of sender */
            var nodeUid: String? = null,
            /** Unique identifier of this file */
            val fileUid: UUID? = null,
            /** Zero-based index of fragment */
            var index: Int = 0,
            /** Total amount of fragments of the file this fragment belongs to */
            var total: Int? = null,
            /** Payload of this fragment */
            var payload: ByteArray? = null
    ) {
        override fun toString(): String {
            return "${this.javaClass.simpleName}(nodeUid=${nodeUid}, fileUid=${fileUid}, index=${index}, total=${total}, payload.size=${payload?.size})"
        }
    }
}

/**
 * Creates file fragment messages from in-memory data
 */
fun ByteArray.toFileFragmentMessages(
        maxChunkSize: Int,
        nodeUid: String,
        fileUid: UUID
): List<FileServiceV1.FileFragmentMessage> {
    val chunks = this.chunked(maxChunkSize)

    return chunks.mapIndexed { index, bytes ->
        FileServiceV1.FileFragmentMessage(
                nodeUid = nodeUid,
                index = index,
                total = chunks.size,
                fileUid = fileUid,
                payload = bytes)
    }
}
