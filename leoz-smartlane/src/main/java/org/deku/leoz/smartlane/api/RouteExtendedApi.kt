package org.deku.leoz.smartlane.api

import com.fasterxml.jackson.databind.JsonNode
import io.reactivex.Observable
import io.swagger.annotations.Api
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.deku.leoz.smartlane.SmartlaneApi
import org.deku.leoz.smartlane.model.Error
import org.deku.leoz.smartlane.model.Processstatus
import org.deku.leoz.smartlane.model.Route
import org.deku.leoz.smartlane.model.Routemetadatas
import org.deku.leoz.smartlane.model.Routinginput
import org.slf4j.LoggerFactory
import sx.log.slf4j.trace
import sx.rs.FlaskFilter
import sx.rs.FlaskOperator
import sx.rs.FlaskQuery
import sx.rx.retryWith
import sx.text.toHexString
import java.util.concurrent.TimeUnit
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.Response

/**
 * Extended smartlane route api
 * Created by masc on 17.11.17.
 */

@Path("/api")
interface RouteExtendedApi : RouteApi {

    data class Status(
            var status: String = "",
            var message: String = "",
            var success: Boolean = false,
            var processId: String = ""
    )

    enum class ProcessStatusType(val value: String) {
        STARTED("STARTED"),
        PENDING("PENDING"),
        SUCCESS("SUCCESS")
    }

    data class ProcessStatus(
            var status: ProcessStatusType? = null,
            var meta: JsonNode? = null
    )

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
    fun postCalcrouteOptimizedTimewindowWithResponse(
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
    fun getProcessStatusByIdByIdWithResponse(
            @PathParam("process_id") processId: String,
            @PathParam("action_id") actionId: String,
            @QueryParam("response_data") @DefaultValue("false") responseData: String?)
            : Response

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

private val log = LoggerFactory.getLogger(RouteExtendedApi::class.java)

/** Exception thrown when process is pending */
class PendingException : Exception()

/**
 * Extension for fetching routes. Uses paging to prevent timeouts on large results.
 * Created by masc on 21.11.17.
 */
fun RouteApi.getRoute(q: String): Observable<Route> {
    val pagesize = 20

    return Observable.create<Route> { emitter ->
        this.getRoute(q, pagesize, 1).let { result ->
            try {
                result.objects.forEach { emitter.onNext(it) }

                if (result.totalPages > 1) {
                    (2..result.totalPages).map { page ->
                        this.getRoute(q, pagesize, page)
                                .objects.forEach { emitter.onNext(it) }
                    }
                }

                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}

fun RouteApi.getRouteByCustomId(customId: String): Observable<Route> {
    return this.getRoute(
            q = FlaskFilter(FlaskQuery(
                    name = "custom_id",
                    op = FlaskOperator.EQ,
                    value = customId
            )).toJson()
    )
}

fun RouteExtendedApi.deleteAll() {
    try {
        this.deleteRoute(q = "{}")
    } catch (e: WebApplicationException) {
        when (e.response.status) {
        // Expected response when query doesn't match anything to delete
            Response.Status.INTERNAL_SERVER_ERROR.statusCode -> return
            else -> throw e
        }
    }
}

/**
 * Normalized route calculation entry point, handling response & status as expected
 */
fun RouteExtendedApi.postCalcrouteOptimizedTimewindowWithStatus(
        body: Routinginput,
        truck: Boolean = false,
        roundtrip: Boolean = false,
        traffic: Boolean = true,
        cancelroutes: Boolean = false,
        vehicle: String? = null,
        numvehicles: Int = 0,
        assignDrivers: Boolean = false,
        strict: Boolean = false
): RouteExtendedApi.Status {
    return this.postCalcrouteOptimizedTimewindowWithResponse(
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
                it.readEntity(RouteExtendedApi.Status::class.java)

            else ->
                throw IllegalStateException("Unexpected status code ${it.status}")
        }
    }
}

/**
 * Normalized process status request
 * @param processId Process id
 */
fun RouteExtendedApi.getProcessStatusById(processId: String): RouteExtendedApi.RouteProcessStatus {
    return this.getProcessStatusByIdByIdWithResponse(
            processId = processId,
            actionId = RouteExtendedApi.ACTION_FINAL,
            responseData = null
    )
            .let { rsp ->
                // Generic http errors
                if (rsp.statusInfo.family != Response.Status.Family.SUCCESSFUL)
                    throw WebApplicationException(rsp)

                when (rsp.status) {
                    Response.Status.ACCEPTED.statusCode -> {
                        val processStatus = rsp.readEntity(RouteExtendedApi.ProcessStatus::class.java)

                        when (processStatus.status) {
                        // Operation still pending
                            RouteExtendedApi.ProcessStatusType.STARTED -> {
                                throw PendingException()
                            }

                            RouteExtendedApi.ProcessStatusType.PENDING -> {
                                throw PendingException()
                            }

                        // Operation completed
                            RouteExtendedApi.ProcessStatusType.SUCCESS -> {
                                // Parse route process status
                                SmartlaneApi.mapper.treeToValue(
                                        processStatus.meta,
                                        RouteExtendedApi.RouteProcessStatus::class.java
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

/**
 * Optimize route
 * @param routingInput Smartlane routing input
 * @param traffic Consider traffic
 * @return Observable
 */
fun RouteExtendedApi.optimize(
        routingInput: Routinginput,
        truck: Boolean = false,
        roundtrip: Boolean = false,
        traffic: Boolean = true,
        cancelroutes: Boolean = false,
        vehicle: String? = null,
        numvehicles: Int = 0,
        assignDrivers: Boolean = false,
        strict: Boolean = false
): Observable<List<Route>> {

    val id = routingInput.hashCode().toHexString()

    return Observable.fromCallable {
        log.trace { "[${id}] Requesting route" }

        // Start async route calculation
        this.postCalcrouteOptimizedTimewindowWithStatus(
                body = routingInput,
                truck = truck,
                roundtrip = roundtrip,
                traffic = traffic,
                cancelroutes = cancelroutes,
                vehicle = vehicle,
                numvehicles = numvehicles,
                assignDrivers = assignDrivers,
                strict = strict
        )
    }
            .flatMap { status ->
                Observable.fromCallable<RouteExtendedApi.RouteProcessStatus> {
                    // Poll status
                    log.trace { "[${id}] Requesting status" }

                    this.getProcessStatusById(
                            processId = status.processId
                    )
                }
                        .retryWith(
                                count = Short.MAX_VALUE,
                                action = { _, e ->
                                    when (e) {
                                    // Retry when pending
                                        is PendingException -> {
                                            log.trace { "[${id}] Pending" }

                                            Observable.timer(1, TimeUnit.SECONDS)
                                        }
                                        else -> throw e
                                    }
                                }
                        )
                        .map {
                            // Get the final result
                            it.routeIds.map {
                                this.getRouteById(it)
                            }
                        }
            }
}