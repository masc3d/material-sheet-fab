package org.deku.leoz.smartlane.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.deku.leoz.smartlane.model.Address
import org.deku.leoz.smartlane.model.Error
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