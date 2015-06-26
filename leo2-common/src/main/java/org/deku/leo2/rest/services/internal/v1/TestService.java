package org.deku.leo2.rest.services.internal.v1;

import org.deku.leo2.rest.entities.internal.v1.TestEntry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Test service
 * Created by masc on 23.07.14.
 */
@Path("internal/v1/test")
@Consumes(MediaType.APPLICATION_JSON)
public interface TestService {
    /** Get all tours */
    @GET
    @Path("/")
    TestEntry[] get();

    /** Get entry by name
     * @param name name */
    @GET
    @Path("/{name}")
    TestEntry get(@PathParam("name") String name);

    /** Find entries
     * @param query Query string */
    @GET
    @Path("/find")
    TestEntry[] find(@QueryParam("q") String query);
}
