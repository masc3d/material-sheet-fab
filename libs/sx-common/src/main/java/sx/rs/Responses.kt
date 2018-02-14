package sx.rs

import java.io.File
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by masc on 14.02.18.
 */

/**
 * Set headers for attachement content disposition
 *
 * @param entity The representation entity data
 * @param filename Filename
 * @param length Content length
 */
fun Response.ResponseBuilder.attachment(
        entity: Any,
        filename: String,
        length: Long? = null
): Response.ResponseBuilder {

    return this
            .type(MediaType.APPLICATION_OCTET_STREAM)
            .entity(entity)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${filename}\"")
            .let {
                when {
                    length != null -> it.header(HttpHeaders.CONTENT_LENGTH, length.toString())
                    else -> it
                }
            }
}

/**
 * Set headers for attachement content disposition
 *
 * @param file File
 * @param filename Override filename. Defaults to @file.name
 */
fun Response.ResponseBuilder.attachment(
        file: File,
        filename: String = file.name
): Response.ResponseBuilder {

    return this
            .attachment(entity = file,
                    filename = filename,
                    length = file.length()
            )
}
