package org.deku.leoz.smartlane.api

import com.fasterxml.jackson.databind.JsonNode
import io.reactivex.Observable
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.deku.leoz.smartlane.SmartlaneApi
import org.deku.leoz.smartlane.model.Delivery
import org.deku.leoz.smartlane.model.Error
import org.deku.leoz.smartlane.model.Processstatus
import org.deku.leoz.smartlane.model.Route
import org.deku.leoz.smartlane.model.Routemetadatas
import org.deku.leoz.smartlane.model.Routinginput
import org.slf4j.LoggerFactory
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

/**
 * Extension for fetching routes. Uses paging to prevent timeouts on large results.
 * Created by masc on 21.11.17.
 */
fun RouteApi.getRoute(q: String): Observable<Route> {
    val pagesize = 20

    return Observable.create<Route> { emitter ->
        this.getRoute(q, pagesize, 1).let { result ->
            result.objects.forEach { emitter.onNext(it) }

            if (result.totalPages > 1) {
                (2..result.totalPages).map { page ->
                    this.getRoute(q, pagesize, page)
                            .objects.forEach { emitter.onNext(it) }
                }
            }
        }
    }
}

/**
 * Manually maintained entry points delivering a generic response
 * Created by masc on 17.11.17.
 */
@Path("/api")
@Api(value = "/", description = "")
interface RouteApiGeneric {

    @ApiModel
    data class Status(
            var status: String = "",
            var message: String = "",
            var success: Boolean = false,
            var processId: String = ""
    )

    enum class ProcessStatusType(val value: String) {
        PENDING("PENDING"),
        SUCCESS("SUCCESS")
    }

    @ApiModel
    data class ProcessStatus(
            var status: ProcessStatusType? = null,
            var meta: JsonNode? = null
    )

    @ApiModel
    data class RouteProcessStatus(
            var message: String = "",
            var success: Boolean = false,
            var successFiles: String? = null,
            var routeIds: List<Int> = listOf()
    )

    companion object {
        val ACTION_FINAL = "final"
    }

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
            @QueryParam("strict") @DefaultValue("false") strict: Boolean?,
            @QueryParam("async") @DefaultValue("true") async: Boolean?,
            @QueryParam("response_data") @DefaultValue("false") responseData: String?)
            : Response

    @GET
    @Path("/process/status/{process_id}/{action_id}")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Process status (id,id)", tags = arrayOf("Route"))
    @ApiResponses(value = *arrayOf(ApiResponse(code = 200, message = "If URL parameter 'response_data' is set to 'all' or 'base': current status of the requested action and also payload data of all calculated routes", response = Routemetadatas::class), ApiResponse(code = 202, message = "If URL parameter 'response_data' is set to a value which is NOT 'all' or 'base': current status of the requested action within the requested routing process", response = Processstatus::class), ApiResponse(code = 403, message = "A failure message caused by missing authorization (403 forbidden)", response = String::class), ApiResponse(code = 422, message = "A failure message caused by unprocessable input (e.g. no data found for input parameters)", response = String::class), ApiResponse(code = 200, message = "Unexpected error", response = Error::class)))
    fun getProcessStatusByIdById(
            @PathParam("process_id") processId: String,
            @PathParam("action_id") actionId: String,
            @QueryParam("response_data") @DefaultValue("false") responseData: String?)
            : Response
}

private val log = LoggerFactory.getLogger(RouteApiGeneric::class.java)

/** Exception thrown when process is pending */
class PendingException : Exception()

/**
 * Calc route optimized timewindow asynchronously
 */
fun RouteApiGeneric.postCalcrouteOptimizedTimewindowAsync(
        body: Routinginput,
        truck: Boolean = false,
        roundtrip: Boolean = false,
        traffic: Boolean = true,
        cancelroutes: Boolean = false,
        vehicle: String? = null,
        numvehicles: Int = 0,
        assignDrivers: Boolean = false,
        strict: Boolean = false
): RouteApiGeneric.Status {
    return this.postCalcrouteOptimizedTimewindow(
            body = body,
            truck = truck,
            roundtrip = roundtrip,
            traffic = traffic,
            cancelroutes = cancelroutes,
            vehicle = vehicle,
            numvehicles = numvehicles,
            assignDrivers = assignDrivers,
            strict = strict,
            async = true,
            responseData = null
    ).let {
        if (it.statusInfo.family != Response.Status.Family.SUCCESSFUL)
            throw WebApplicationException(it)

        when (it.status) {
            Response.Status.ACCEPTED.statusCode ->
                it.readEntity(RouteApiGeneric.Status::class.java)

            else ->
                throw IllegalStateException("Unexpected status code ${it.status}")
        }
    }
}

fun RouteApiGeneric.getProcessStatusById(processId: String): RouteApiGeneric.RouteProcessStatus {
    return this.getProcessStatusByIdById(
            processId = processId,
            actionId = RouteApiGeneric.ACTION_FINAL,
            responseData = null
    )
            .let { rsp ->
                // Generic http errors
                if (rsp.statusInfo.family != Response.Status.Family.SUCCESSFUL)
                    throw WebApplicationException(rsp)

                when (rsp.status) {
                    Response.Status.ACCEPTED.statusCode -> {
                        val processStatus = rsp.readEntity(RouteApiGeneric.ProcessStatus::class.java)

                        when (processStatus.status) {
                        // Operation still pending
                            RouteApiGeneric.ProcessStatusType.PENDING -> {
                                throw PendingException()
                            }

                        // Operation completed
                            RouteApiGeneric.ProcessStatusType.SUCCESS -> {
                                // Parse route process status
                                SmartlaneApi.mapper.treeToValue(
                                        processStatus.meta,
                                        RouteApiGeneric.RouteProcessStatus::class.java
                                ).also {
                                    if (it.success == false)
                                        throw IllegalStateException(it.message)
                                }
                            }
                            else -> {
                                throw IllegalStateException("Unexpected process status ${processStatus.status}")
                            }
                        }
                    }

                    else ->
                        throw IllegalStateException("Unexpected status code ${rsp.status}")
                }
            }
}

