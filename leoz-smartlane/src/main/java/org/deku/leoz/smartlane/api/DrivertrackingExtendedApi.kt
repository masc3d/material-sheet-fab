package org.deku.leoz.smartlane.api

import javax.ws.rs.*
import javax.ws.rs.core.Response

/**
 * Smartlane drivertracking api extensions
 * Created by masc on 02.02.18.
 */

@Path("/api")
interface DrivertrackingExtendedApi : DriverApi{
    /**
     * Delete drivers
     */
    @DELETE
    @Path("/drivertracking")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteDrivertracking(@QueryParam("q") q: String)
}

fun DrivertrackingExtendedApi.deleteAll() {
    try {
        this.deleteDrivertracking(q = "{}")
    } catch(e: WebApplicationException) {
        when (e.response.status) {
        // Expected response when query doesn't match anything to delete
            Response.Status.INTERNAL_SERVER_ERROR.statusCode -> return
            else -> throw e
        }
    }
}