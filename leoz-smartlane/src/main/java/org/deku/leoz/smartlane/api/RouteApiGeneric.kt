package org.deku.leoz.smartlane.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.deku.leoz.smartlane.model.Error
import org.deku.leoz.smartlane.model.Routemetadatas
import org.deku.leoz.smartlane.model.Routinginput
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.DefaultValue
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

/**
 * Manually maintained entry points delivering a generic response
 * Created by masc on 17.11.17.
 */
@Path("/api")
@Api(value = "/", description = "")
interface RouteApiGeneric {
    @POST
    @Path("/calcroute/optimized/timewindow")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Calc route optimized timewindow", tags = arrayOf("Route"))
    @ApiResponses(value = *arrayOf(ApiResponse(code = 200, message = "resulting route ids and (if reponse_data is set to 'base' or 'all'), also route payload data. 'all' also includes geo routestring of tour. Only relevant for synchroneous routing (else, see response status 202)", response = Routemetadatas::class), ApiResponse(code = 202, message = "process_id for started asynchroneous process. Can be used for async status polling via \"GET /api/process/status/<process_id>/final\""), ApiResponse(code = 403, message = "A failure message caused by missing authorization (403 forbidden)", response = String::class), ApiResponse(code = 422, message = "A failure message caused by unprocessable input (e.g. no data found for input parameters)", response = String::class), ApiResponse(code = 200, message = "Unexpected error", response = Error::class)))
    fun postCalcrouteOptimizedTimewindow(
            @Valid body: Routinginput,
            @QueryParam("truck") @DefaultValue("false") truck: Boolean?,
            @QueryParam("roundtrip") @DefaultValue("settings parameter for ROUNDTRIP_TOUR (which defaults to false)") roundtrip: Boolean?,
            @QueryParam("traffic") @DefaultValue("true") traffic: Boolean?,
            @QueryParam("cancelroutes") @DefaultValue("false") cancelroutes: Boolean?,
            @QueryParam("vehicle") @DefaultValue("car") vehicle: String?,
            @QueryParam("numvehicles") @DefaultValue("0") numvehicles: Int?,
            @QueryParam("assign_drivers") @DefaultValue("false") assignDrivers: Boolean?,
            @QueryParam("strict") @DefaultValue("false") strict: Boolean?, @QueryParam("async") @DefaultValue("true") async: Boolean?,
            @QueryParam("response_data") @DefaultValue("false") responseData: String?): Response
}