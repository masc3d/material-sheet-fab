package sx.rs

import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.rs.client.RestEasyClient
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter
import javax.ws.rs.client.ClientResponseContext
import javax.ws.rs.client.ClientResponseFilter
import javax.ws.rs.ext.WriterInterceptor
import javax.ws.rs.ext.WriterInterceptorContext

/**
 * JAX-RS logging filter
 */
class LoggingFilter  : ClientRequestFilter, ClientResponseFilter, WriterInterceptor {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Logging stream
     */
    private inner class LoggingStream constructor(
            private val inner: OutputStream) : OutputStream() {

        private val DEFAULT_MAX_ENTITY_SIZE = 8 * 1024

        private val maxEntitySize: Int = DEFAULT_MAX_ENTITY_SIZE

        /** Stream content */
        val content: StringBuilder by lazy {
            val entity = buffer.toByteArray()

            StringBuilder().also {
                it.append(String(entity, 0, Math.min(entity.size, maxEntitySize)))
                if (entity.size > maxEntitySize) {
                    it.append("...more...")
                }
            }
        }

        /** Internal buffer */
        private val buffer = ByteArrayOutputStream()

        /** Current (content) length of this stream */
        val length get() = this.buffer.size()

        override fun write(i: Int) {
            if (buffer.size() <= maxEntitySize) {
                buffer.write(i)
            }
            inner.write(i)
        }
    }

    override fun aroundWriteTo(context: WriterInterceptorContext) {
        context.proceed()

        val stream = context.outputStream as LoggingStream
        if (stream.length > 0) {
            log.trace { "REQUEST ${stream.content}" }
        }
    }

    override fun filter(requestContext: ClientRequestContext) {
        //region Request logging
        log.trace { requestContext.uri }

        if (requestContext.hasEntity()) {
            log.trace { "REQUEST ENTITY ${requestContext.entity}" }

            requestContext.entityStream = LoggingStream(requestContext.entityStream)
        }
        //endregion
    }

    override fun filter(requestContext: ClientRequestContext, responseContext: ClientResponseContext) {
        //region Response logging
        log.trace {
            val data = responseContext.entityStream?.readBytes()
            responseContext.entityStream = data?.let { ByteArrayInputStream(data) }

            "RESPONSE ${data?.toString(charset = Charsets.UTF_8)}"
        }
        //endregion
    }
}