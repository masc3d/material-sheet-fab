package org.deku.leoz.rest.services.internal.v1

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import org.deku.leoz.rest.entities.internal.v1.ApplicationVersion
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.MediaType

/**
 * Created by JT on 05.02.16.
 */
@Path("internal/v1/central")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Central operations")
interface CentralService {

    @GET
    @Path("/database-sync")
    @ApiOperation(value = "Push Databasesync") //, response = String::class)
    fun databaseSync()
}