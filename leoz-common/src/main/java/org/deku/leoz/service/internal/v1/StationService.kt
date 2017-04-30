package org.deku.leoz.service.internal.v1

import io.swagger.annotations.*
import org.deku.leoz.service.entity.internal.v1.Station
import javax.ws.rs.*

import javax.ws.rs.core.MediaType

/**
 * Created by masc on 17.09.14.
 */
@javax.ws.rs.Path("internal/v1/depot")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Depot operations")
interface StationService {
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    @io.swagger.annotations.ApiOperation(value = "Get all depots", notes = "Some notes", response = org.deku.leoz.service.entity.internal.v1.Station::class)
    fun get(): Array<org.deku.leoz.service.entity.internal.v1.Station>

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/find")
    @io.swagger.annotations.ApiOperation(value = "Query depots by simple substring match applied to relevant fields")
    @io.swagger.annotations.ApiResponses(value = *arrayOf(
            io.swagger.annotations.ApiResponse(code = 404, message = "No depots found")
    ))
    fun find(
            @io.swagger.annotations.ApiParam(value = "Query string") @javax.ws.rs.QueryParam("q") query: String): Array<org.deku.leoz.service.entity.internal.v1.Station>
}
