package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import sx.rs.auth.ApiKey
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