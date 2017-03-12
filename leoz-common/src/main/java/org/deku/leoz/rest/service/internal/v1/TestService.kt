package org.deku.leoz.rest.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.rest.entity.internal.v1.ApplicationVersion
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

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