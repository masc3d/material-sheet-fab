package org.deku.leoz.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@javax.ws.rs.Path("internal/v1/central")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Central operations")
interface CentralService {

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/sync")
    @io.swagger.annotations.ApiOperation(value = "Trigger central database sync")
    fun sync(@javax.ws.rs.QueryParam("clean") @io.swagger.annotations.ApiParam(defaultValue = "false", value = "Perform clean sync (drop existing data)") clean: Boolean)
}