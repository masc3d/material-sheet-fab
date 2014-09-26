package org.deku.leo2.bridge;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * LeoBridge service interface
 *
 * Created by masc on 17.09.14.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public interface IMessageService {
    @GET
    @Path("/send")
    boolean send(@QueryParam("m") String message);
}
