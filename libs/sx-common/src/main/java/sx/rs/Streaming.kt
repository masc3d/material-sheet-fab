package sx.rs

import io.reactivex.Observable
import java.io.OutputStream
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.StreamingOutput

/**
 * Created by masc on 14.02.18.
 */

/**
 * Convert observable strings to jax/rs streaming output
 */
fun Observable<String>.toStreamingOutput(): StreamingOutput {
    return object : StreamingOutput {
        override fun write(output: OutputStream) {
            try {
                val writer = output.buffered().writer()

                // There's no non-blocking in jax/rs :/
                this@toStreamingOutput
                        .doOnNext { writer.write(it) }
                        .doFinally { writer.flush() }
                        .blockingSubscribe()

            } catch (e: Throwable) {
                throw WebApplicationException(e)
            }
        }
    }
}