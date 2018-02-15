package sx.rs

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOperator
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.IOException
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
                        .lift<String> {
                            object : Observer<String> {
                                var d: Disposable? = null

                                // Pass-through
                                override fun onComplete() { it.onComplete() }
                                override fun onSubscribe(d: Disposable) { this.d = d; it.onSubscribe(d) }
                                override fun onError(e: Throwable) { it.onError(e) }

                                override fun onNext(t: String) {
                                    try {
                                        writer.write(t)
                                        it.onNext(t)
                                    } catch (e: Throwable) {
                                        // Ignore errors on writing, immediately dispose upstream and complete
                                        d?.dispose()
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