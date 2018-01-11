package org.deku.leoz.service.internal

import com.github.salomonbrys.kodein.Kodein
import io.reactivex.Observable
import org.deku.leoz.config.RestClientTestConfiguration
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.media.sse.EventSource
import org.glassfish.jersey.media.sse.InboundEvent
import org.glassfish.jersey.media.sse.SseFeature
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.junit.Test
import org.junit.experimental.categories.Category
import org.slf4j.LoggerFactory
import sx.log.slf4j.info
import sx.log.slf4j.trace
import javax.ws.rs.sse.InboundSseEvent
import javax.ws.rs.sse.SseEventSource

/**
 * Created by masc on 11.01.18.
 */
@Category(sx.junit.PrototypeTest::class)
class TourServiceTest {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val kodein = Kodein {
        import(RestClientTestConfiguration.module)
    }

    @Test
    fun testStatusJersey() {
        var error: Throwable? = null

        Observable.create<InboundEvent> { emitter ->
            JerseyClientBuilder().register(SseFeature::class.java).build().also { client ->
                EventSource.target(client
                        .target("http://localhost:13000/rs/api/internal/v1/tour/optimize/status/sse")
                        .queryParam("station-id", 1)
                )
                        .build()
                        .apply {
                            register({ event ->
                                emitter.onNext(event)
                            })
                        }
                        .open()
            }
        }
                .blockingSubscribe({
                    log.info { "RECEIVED ${it}" }
                }, { e ->
                    error = e
                })

        error?.also { throw it }
    }

    @Test
    fun testStatusResteasy() {
        var error: Throwable? = null

        Observable.create<InboundSseEvent> { emitter ->
            ResteasyClientBuilder.newBuilder().build().also { client ->
                SseEventSource.target(client
                        .target("http://localhost:13000/rs/api/internal/v1/tour/optimize/status/sse")
                        .queryParam("station-id", 1)
                )
                        .build()
                        .apply {
                            register({ event ->
                                emitter.onNext(event)
                            }, { e ->
                                emitter.onError(e)
                            })
                        }
                        .open()
            }
        }
                .blockingSubscribe({
                    log.info { "RECEIVED ${it}" }
                }, { e ->
                    error = e
                })

        error?.also { throw it }
    }
}