package org.deku.leoz.smartlane.api

import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.rs.FlaskFilter
import sx.rs.FlaskQuery
import sx.rs.FlaskOperator
import javax.ws.rs.*

/**
 * Created by masc on 13.01.18.
 */
@Path("/api")
interface InternalApi {
    /**
     * Delete address by id
     */
    @DELETE
    @Path("/address/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteAddress(@PathParam("id") id: Int?)

    /**
     * Delete addresses
     */
    @DELETE
    @Path("/address")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteAddress(@QueryParam("q") q: String)

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

    /**
     * Delete route by id
     */
    @DELETE
    @Path("/route/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteRoute(@PathParam("id") id: Int?)

    /**
     * Delete routes
     */
    @DELETE
    @Path("/route")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteRoute(@QueryParam("q") q: String)
}

private val log by lazy { LoggerFactory.getLogger(InternalApi::class.java) }

fun InternalApi.deleteAddressesNotIn(ids: Iterable<Long>) {
    val filter = FlaskFilter(
            filters = listOf(
                    FlaskQuery(
                            name = "id",
                            op = FlaskOperator.NOT_IN,
                            value = ids
                    )
            )
    )

    log.trace { filter.toJson() }

    this.deleteAddress(q = filter.toJson())
}

fun InternalApi.deleteAllRoutes() {
    this.deleteRoute(q = "{}")
}

fun InternalApi.deleteAllDeliveries() {
    this.deleteDelivery(q = "{}")
}