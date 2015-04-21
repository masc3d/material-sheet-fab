package org.deku.leo2.rest.services.v1;

import org.deku.leo2.rest.entities.v1.LocalDateParam;
import org.deku.leo2.rest.entities.v1.Routing;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/routing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface RoutingService {

    @GET
    @Path("/find")
    Routing[] find(@QueryParam("date") LocalDateParam date,
                 @QueryParam("country") String country,
                 @QueryParam("zip") String zip,
                 @QueryParam("product") String product);

    @GET
    @Path("/find/via")
    String[] findVia(@QueryParam("date") LocalDateParam date,
                    @QueryParam("source_sector") String sourceSector,
                    @QueryParam("destination_sector") String destinationSector);
}
