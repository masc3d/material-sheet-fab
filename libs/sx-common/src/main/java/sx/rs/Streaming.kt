package sx.rs

import io.reactivex.Observable
import io.reactivex.observers.DefaultObserver
import org.slf4j.LoggerFactory
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
    val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    return object : StreamingOutput {
        override fun write(output: OutputStream) {
            try {
                val writer = output.buffered().writer()

                // There's no non-blocking in jax/rs :/

                this@toStreamingOutput
                        .lift<String> {
                            object : DefaultObserver<String>() {
                                // Pass-through
                                override fun onComplete() { it.onComplete() }
                                override fun onError(e: Throwable) { it.onError(e) }

                                override fun onNext(t: String) {
                                    try {
                                        writer.write(t)
                                        it.onNext(t)
                                    } catch (e: Throwable) {
                                        log.error(e.message, e)
                                        // On write errors, immediately dispose upstream
                                        this.cancel()
                                        // ..and complete instead
                                        it.onComplete()
                                    }
                                }
                            }
                        }
                        .doFinally {
                            try {
                                writer.flush()
                            } catch (e: Throwable) {
                                // Ignore errors on flushing
                            }
                        }
                        .blockingSubscribe()
            } catch (e: Throwable) {
                throw WebApplicationException(e)
            }
        }
    }
}