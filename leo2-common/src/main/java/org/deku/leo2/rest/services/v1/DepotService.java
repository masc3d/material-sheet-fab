package org.deku.leo2.rest.services.v1;

import org.deku.leo2.rest.entities.v1.Depot;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by masc on 17.09.14.
 */
@Path("v1/depot")
@Consumes(MediaType.APPLICATION_JSON)
public interface DepotService {
    @GET
    @Path("/")
    Depot[] get();

    @GET
    @Path("/find")
    Depot[] find(@QueryParam("q") String query);
}
