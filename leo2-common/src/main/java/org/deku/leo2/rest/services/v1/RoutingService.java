package org.deku.leo2.rest.services.v1;

import com.wordnik.swagger.annotations.*;
import org.deku.leo2.rest.adapters.LocalDateParam;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingVia;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Routing operations")
public interface RoutingService {

    @GET
    @Path("/find")
    @ApiOperation(value = "Determine routing")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No depots found")
    })
    Routing find(@QueryParam("date")
                 @ApiParam(value = "Date")
                 LocalDateParam date,
                 @QueryParam("country")
                 @ApiParam(value = "Country ")
                 String country,
                 @QueryParam("zip")
                 @ApiParam(value = "Zip code") String zip,
                 @QueryParam("product")
                 @ApiParam(value = "Product") String product);

    @GET
    @Path("/find/via")
    @ApiOperation(value = "Determine routing via sector")
    RoutingVia findVia(@QueryParam("date")
                       @ApiParam(value = "Date")
                       LocalDateParam date,
                       @QueryParam("source_sector")
                       @ApiParam(value = "Source Sector")
                       String sourceSector,
                       @QueryParam("destination_sector")
                       @ApiParam(value = "Destination sector")
                       String destinationSector);
}
