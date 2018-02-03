package org.deku.leoz.smartlane.api

import javax.ws.rs.*

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
    this.deleteDrivertracking(q = "{}")
}