package org.deku.leo2.rest.services.v1;

import com.wordnik.swagger.annotations.*;
import org.deku.leo2.rest.entities.ShortDate;
import org.deku.leo2.rest.entities.v1.Participant;
import org.deku.leo2.rest.entities.v1.Routing;
import org.deku.leo2.rest.entities.v1.RoutingRequest;
//import org.deku.leo2.rest.entities.v1.RoutingVia;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Routing operations")
public interface RoutingService {

//    @GET
//    @Path("/find")
//    @ApiOperation(value = "Determine routing")
//    // TODO: not working atm due to bug: https://github.com/swagger-api/swagger-core/issues/905
//    @ApiImplicitParams(
//            @ApiImplicitParam(name = "senddate", value = "Date", required = true, paramType = "query", dataType = "string")
//    )
//    @ApiResponses(value = {
//            @ApiResponse(code = 404, message = "No depots found")
//    })
//    Routing find(@QueryParam("senddate")
//                 @ApiParam(value = "Date (YYYY-MM-DD)")
//                 String date,
//                 //ShortDate date,
//                 @QueryParam("country")
//                 @ApiParam(value = "Country two-letter ISO-3166")
//                 String country,
//                 @QueryParam("zip")
//                 @ApiParam(value = "Zip code accordant to country spezification") String zip,
//                 @QueryParam("product")
//                 @ApiParam(value = "Optional: Product") String product);

    @POST
    @Path("/request")
    @ApiOperation(value = "Determ routing and via")
    Routing request(@ApiParam(value="Routing request") RoutingRequest routingRequest);

//    @GET
//    @Path("/find/via")
//    @ApiOperation(value = "Determine routing via sector")
//    RoutingVia findVia(@QueryParam("date")
//                       @ApiParam(value = "Date")
//                       ShortDate date,
//                       @QueryParam("source_sector")
//                       @ApiParam(value = "Source Sector")
//                       String sourceSector,
//                       @QueryParam("destination_sector")
//                       @ApiParam(value = "Destination sector")
//                       String destinationSector);
}
