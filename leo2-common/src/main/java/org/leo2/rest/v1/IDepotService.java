package org.leo2.rest.v1;

import org.leo2.rest.v1.entities.Depot;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}
