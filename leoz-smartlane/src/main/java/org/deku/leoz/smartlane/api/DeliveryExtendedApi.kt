package org.deku.leoz.smartlane.api

import io.reactivex.Observable
import org.deku.leoz.smartlane.model.Deliveries
import org.deku.leoz.smartlane.model.Delivery
import org.slf4j.LoggerFactory
import javax.ws.rs.*
import javax.ws.rs.core.Response


/**
 * Extended smartlane delivery api
 * Created by masc on 13.01.18.
 */

@Path("/api")
interface DeliveryExtendedApi : DeliveryApi {
    /**
     * Delete delivery by id
     */
    @DELETE
    @Path("/delivery/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteDelivery(@PathParam("id") id: Int?)

    /**
     * Delete deliveries
     */
    @DELETE
    @Path("/delivery")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteDelivery(@QueryParam("q") q: String)
}

/**
 * Extension for fetching deliveries. Uses paging to prevent timeouts on large results.
 * Created by masc on 21.11.17.
 */
fun DeliveryApi.getDelivery(q: String): Observable<Delivery> {
    val pagesize = 20

    return Observable.create<Delivery> { emitter ->
        try {
            this.getDelivery(q, pagesize, 1).let { result ->
                result.objects.forEach { emitter.onNext(it) }

                if (result.totalPages > 1) {
                    (2..result.totalPages).map { page ->
                        this.getDelivery(q, pagesize, page)
                                .objects.forEach { emitter.onNext(it) }
                    }
                }
            }
            emitter.onComplete()
        } catch(e: Exception) {
            emitter.onError(e)
        }
    }
}

fun DeliveryExtendedApi.deleteAll() {
    try {
        this.deleteDelivery(q = "{}")
    } catch(e: WebApplicationException) {
        when (e.response.status) {
            // Expected response when query doesn't match anything to delete
            Response.Status.INTERNAL_SERVER_ERROR.statusCode -> return
            else -> throw e
        }
    }
}