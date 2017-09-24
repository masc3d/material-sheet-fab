package org.deku.leoz.service.internal

import javax.ws.rs.core.*
import javax.ws.rs.*
import io.swagger.annotations.*
import sx.rs.PATCH
import sx.rs.auth.ApiKey

/**
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Test operations")
@ApiKey(false)
interface TestService {
    /**
     * Get entry by name
     * @param name name
     */
    @GET
    @Path("/test-soap-call")
    @ApiOperation(value = "Invoke soap call for testing")
    fun testSoapCall(): Response

    @PATCH
    @Path("/test-publish-updateinfo-mobile")
    fun testPublishUpdateInfoToMobile()
}