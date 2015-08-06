package org.deku.leo2.bridge;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * LeoBridge service interface
 *
 * Created by masc on 17.09.14.
 */
@Produces(MediaType.APPLICATION_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON_UTF8)
@Path("/")
public interface IMessageService {
    @POST
    @Path("/send")
    void send(Message message);
}
