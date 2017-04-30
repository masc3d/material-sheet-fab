package org.deku.leoz.service.internal

import javax.ws.rs.core.*
import javax.ws.rs.*
import io.swagger.annotations.*

/**
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Test operations")
interface TestService {
    /**
     * Get entry by name
     * @param name name
     */
    @GET
    @Path("/test-soap-call")
    @ApiOperation(value = "Invoke soap call for testing")
    fun testSoapCall(): Response
}