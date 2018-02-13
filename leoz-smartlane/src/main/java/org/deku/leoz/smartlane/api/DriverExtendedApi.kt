package org.deku.leoz.smartlane.api

import org.deku.leoz.smartlane.model.Driver
import sx.rs.FlaskOperator
import sx.rs.FlaskPredicate
import javax.ws.rs.*
import javax.ws.rs.core.Response

/**
 * Smartlane driver api extensions
 * Created by masc on 02.02.18.
 */

@Path("/api")
interface DriverExtendedApi : DriverApi{
    /**
     * Delete drivers
     */
    @DELETE
    @Path("/driver")
    @Consumes("application/json")
    @Produces("application/json")
    fun deleteDriver(@QueryParam("q") q: String)
}

/**
 * Get driver by email
 * @param email Email
 */
fun DriverApi.getDriverByEmail(email: String): Driver? {
    return this.getDriver(sx.rs.FlaskFilter(
            FlaskPredicate(
                    name = "email",
                    op = FlaskOperator.EQ,
                    value = email)
    ).toJson(),
            true,
            true,
            true
    ).objects.firstOrNull()
}

fun DriverExtendedApi.deleteAll() {
    try {
        this.deleteDriver(q = "{}")
    } catch(e: WebApplicationException) {
        when (e.response.status) {
        // Expected response when query doesn't match anything to delete
            Response.Status.INTERNAL_SERVER_ERROR.statusCode -> return
            else -> throw e
        }
    }
}