package org.deku.leoz.smartlane.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.deku.leoz.smartlane.model.Address
import org.deku.leoz.smartlane.model.Error
import org.deku.leoz.smartlane.model.Tourarea
import javax.ws.rs.*

/**
 * Created by masc on 10.01.18.
 */
@Path("/api")
@Api(value = "/", description = "")
interface AddressInternalApi {
    /**
     * Delete address (id)
     */
    @DELETE
    @Path("/address/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Delete address (id)", tags = arrayOf("Address"))
    @ApiResponses(value = [(ApiResponse(code = 200, message = "OK", response = Address::class)), (ApiResponse(code = 403, message = "A failure message caused by missing authorization (403 forbidden)", response = String::class)), (ApiResponse(code = 422, message = "A failure message caused by unprocessable input (e.g. no data found for input parameters)", response = String::class)), (ApiResponse(code = 200, message = "Unexpected error", response = Error::class))])
    fun delete(@PathParam("id") id: Int?): Address
}