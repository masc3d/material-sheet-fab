package org.deku.leoz.smartlane.api

import io.reactivex.Observable
import org.deku.leoz.smartlane.model.Delivery
import sx.rs.FlaskFilter
import sx.rs.FlaskOperator
import sx.rs.FlaskPredicate
import java.util.*
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
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }
}

fun DeliveryExtendedApi.deleteAll() {
    amendDeleteErrorResponse{
        this.deleteDelivery(q = "{}")
    }
}

fun DeliveryExtendedApi.delete(ids: List<Int>) {
    amendDeleteErrorResponse{
        this.deleteDelivery(q = FlaskFilter(
                expression = FlaskPredicate(
                        name = "id",
                        op = FlaskOperator.IN,
                        value = ids
                )).toJson()
        )
    }
}

/**
 * Remove unreferenced deliveries (which have no route assigned)
 */
fun DeliveryExtendedApi.deleteUnreferenced() {
    amendDeleteErrorResponse {
        this.deleteDelivery(q = FlaskFilter(
                expression = FlaskPredicate(
                        name = "route_id",
                        op = FlaskOperator.IS_NULL,
                        field = "route_id"
                )).toJson()
        )
    }
}

val Delivery.etaFrom: Date?
    get() = this.etaInterval.getOrNull(0)

val Delivery.etaTo: Date?
    get() = this.etaInterval.getOrNull(1)

val Delivery.deliveryFrom: Date?
    get() = this.deliveryInterval.getOrNull(0)

val Delivery.deliveryTo: Date?
    get() = this.deliveryInterval.getOrNull(1)