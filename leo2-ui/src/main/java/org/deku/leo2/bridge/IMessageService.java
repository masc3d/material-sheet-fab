package org.deku.leo2.bridge;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.ws.rs.*;
import java.util.Dictionary;
import java.util.HashMap;

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
