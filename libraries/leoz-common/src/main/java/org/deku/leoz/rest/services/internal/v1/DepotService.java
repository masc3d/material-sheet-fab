package org.deku.leoz.rest.services.internal.v1;

import com.wordnik.swagger.annotations.*;
import org.deku.leoz.rest.entities.internal.v1.Depot;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("internal/v1/depot")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value="Depot operations")
public interface DepotService {
    @GET
    @Path("/")
    @ApiOperation(value = "Get all depots",
            notes = "Some notes",
            response = Depot.class)
    Depot[] get();

    @GET
    @Path("/find")
    @ApiOperation(value = "Query depots by simple substring match applied to relevant fields")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No depots found")
    })
    Depot[] find(
            @ApiParam(value = "Query string") @QueryParam("q") String query);
}
