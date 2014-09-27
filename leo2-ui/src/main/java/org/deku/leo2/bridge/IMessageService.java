package org.deku.leo2.bridge;

import javax.ws.rs.*;

/**
 * LeoBridge service interface
 *
 * Created by masc on 17.09.14.
 */
@Produces(MediaType.APPLICATION_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON_UTF8)
@Path("/")
public interface IMessageService {
    @GET
    @Path("/send")
    boolean send(@QueryParam("m") String message);
}
