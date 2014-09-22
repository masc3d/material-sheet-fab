package org.deku.leo2.rest.v1;

import org.deku.leo2.rest.v1.entities.Depot;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("v1/depot")
public interface IDepotService {
    @GET
    @Path("/")
    Depot[] get();

    @GET
    @Path("/find")
    Depot[] find(@QueryParam("q") String query);
}
