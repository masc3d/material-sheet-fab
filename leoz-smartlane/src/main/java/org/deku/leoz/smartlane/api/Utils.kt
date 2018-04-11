package org.deku.leoz.smartlane.api

import sx.rs.FlaskFilter
import sx.rs.FlaskOperator
import sx.rs.FlaskPredicate
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

/**
 * Smartlane currently emits 500 (internal server error) when
 * provided (flask) criteria doesn't match anything on @DELETE invocations.
 *
 * This helper will handle the referring `WebApplicationException`
 * and ignore internal server errors.
 */
fun amendDeleteErrorResponse(block: () -> Unit) {
    try {
        block()
    } catch (e: WebApplicationException) {
        when (e.response.status) {
        // Expected response when query doesn't match anything to delete
            Response.Status.INTERNAL_SERVER_ERROR.statusCode -> return
            else -> throw e
        }
    }
}