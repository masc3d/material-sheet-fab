package org.leo2.rest.v1;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Test service
 * Created by masc on 23.07.14.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ITestService {
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
