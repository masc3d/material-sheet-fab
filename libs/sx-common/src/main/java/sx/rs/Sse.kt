package sx.rs

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import sx.log.slf4j.info
import java.util.*
import java.util.concurrent.TimeUnit
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink

/**
 * Created by masc on 07.02.18.
 */

/**
 * Subscribes to the event observable and pushes them into the sink.
 *
 * The subscription and sink will be cleaned up automatically on disconnection
 * or when the events observable completes.
 * In order to detect disconnection, ping events will be sent every minute.
 *
 * Doesn't enforce a particular scheduler.
 *
 * @param sse SSE instance
 * @param uuid Unique identifier for this push operation. Defaults to random.
 * @param events Events to push
 * @return Subscription
 */
fun <T : Any> SseEventSink.push(
        sse: Sse,
        uuid: String = UUID.randomUUID().toString(),
        events: Observable<T>,
        onError: (e: Throwable) -> Unit = {}): Disposable {

    val sink = this
    var subscription: Disposable? = null

    /** Helper to close down (with optional error) */
    fun close(error: Throwable? = null) {
        subscription?.dispose()
        sink.close()

        error?.also {
            onError(it)
        }
    }

    subscription = Observable.merge(
            // Emit interval based (ping) event in order to detect remote disconnection
            Observable.interval(1, TimeUnit.MINUTES)
                    .map { sse.newEventBuilder().id(uuid).build() }
            ,
            events.map {
                sse.newEventBuilder()
                        .id(uuid)
                        .mediaType(MediaType.APPLICATION_JSON_TYPE)
                        .data(it.javaClass, it)
                        .build()
            }
    )
            .takeUntil { sink.isClosed }
            .doFinally {
                close()
            }
            .subscribeBy(
                    onNext = { event ->
                        try {
                            // Send SSE event for each update
                            sink.send(event)
                                    .whenComplete { result, e ->
                                        val error: Throwable? = when {
                                            result is Throwable -> result
                                            e != null -> e
                                            else -> null
                                        }

                                        if (error != null)
                                            close(error)
                                    }
                                    .exceptionally { e ->
                                        close(e)
                                        null
                                    }
                        } catch (e: Throwable) {
                            close(e)
                        }
                    },
                    onError = { e ->
                        close(e)
                    }
            )

    return subscription
}
