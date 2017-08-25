package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import org.deku.leoz.model.AdditionalInfo
import sx.io.serialization.Serializable
import java.util.*
import javax.activation.MimeType
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 17.07.17.
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
    data class FileFragmentMessage(
            /** Node uid of sender */
            var nodeUid: String? = null,
            /** Zero-based index of fragment */
            var index: Int = 0,
            /** Total amount of fragments of the file this fragment belongs to */
            var total: Int? = null,
            /** Unique identifier of this file */
            val fileUid: UUID? = null,
            /** Payload of this fragment */
            val payload: ByteArray? = null
    )
}

