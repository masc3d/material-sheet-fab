package org.deku.leo2.rest.services.v1;

import org.deku.leo2.entities.v1.Depot;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/depot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DepotService {
    @GET
    @Path("/")
    Depot[] get();

    @GET
    @Path("/find")
    Depot[] find(@QueryParam("q") String query);
}
