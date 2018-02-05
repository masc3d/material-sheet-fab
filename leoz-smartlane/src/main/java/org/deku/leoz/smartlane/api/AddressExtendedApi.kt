package org.deku.leoz.smartlane.api

import sx.rs.FlaskFilter
import sx.rs.FlaskOperator
import sx.rs.FlaskQuery
import javax.ws.rs.*
import javax.ws.rs.core.Response

/**
 * Extended smartlane address api
 * Created by masc on 13.01.18.
 */

@Path("/api")
interface AddressExtendedApi : AddressApi {
    /**
     * Delete address by id
     */
    @DELETE
    @Path("/address/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    fun delete(@PathParam("id") id: Int?)

    /**
     * Delete addresses
     */
    @DELETE
    @Path("/address")
    @Consumes("application/json")
    @Produces("application/json")
    fun delete(@QueryParam("q") q: String)
}

fun AddressExtendedApi.deleteAddressesNotIn(ids: Iterable<Long>) {
    val filter = FlaskFilter(
            filters = listOf(
                    FlaskQuery(
                            name = "id",
                            op = FlaskOperator.NOT_IN,
                            value = ids
                    )
            )
    )

    try {
        this.delete(q = filter.toJson())
    } catch(e: WebApplicationException) {
        when (e.response.status) {
        // Expected response when query doesn't match anything to delete
            Response.Status.INTERNAL_SERVER_ERROR.statusCode -> return
            else -> throw e
        }
    }
}