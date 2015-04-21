package org.deku.leo2.rest.services.v1;

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
public interface RoutingService {

    @GET
    @Path("/find")
    Routing[] find(@QueryParam("date") LocalDateParam date,
                 @QueryParam("country") String country,
                 @QueryParam("zip") String zip,
                 @QueryParam("product") String product);

    @GET
    @Path("/find/via")
    RoutingVia findVia(@QueryParam("date") LocalDateParam date,
                    @QueryParam("source_sector") String sourceSector,
                    @QueryParam("destination_sector") String destinationSector);
}
