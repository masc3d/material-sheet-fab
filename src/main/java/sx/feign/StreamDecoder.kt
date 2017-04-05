package sx.feign

import feign.Response
import sx.io.copyTo
import java.io.OutputStream
import java.lang.reflect.Type
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

/**
 * Feign (octet-)stream decoder
 * Created by masc on 06/02/2017.
 */
class StreamDecoder(
        private val fallbackDecoder: feign.codec.Decoder,
        private val output: OutputStream,
        private val progressCallback: ((p: Float, bytesCopied: Long) -> Unit)? = null)
    : feign.codec.Decoder {
    override fun decode(response: Response, type: Type): Any? {

        val isOctetStream = response.headers().get(HttpHeaders.CONTENT_TYPE)
                ?.first()
                .equals(MediaType.APPLICATION_OCTET_STREAM, ignoreCase = true)

        return if (isOctetStream) {
            val body = response.body()

            body.asInputStream().copyTo(
                    out = output,
                    length = body.length().toLong(),
                    progressCallback = progressCallback)

            null
        } else {
            this.fallbackDecoder.decode(response, type)
        }
    }
}