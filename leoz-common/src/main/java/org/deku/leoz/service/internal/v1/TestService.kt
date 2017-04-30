package org.deku.leoz.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by masc on 09.10.15.
 */
@javax.ws.rs.Path("internal/v1/test")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Test operations")
interface TestService {
    /**
     * Get entry by name
     * @param name name
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/test-soap-call")
    @io.swagger.annotations.ApiOperation(value = "Invoke soap call for testing")
    fun testSoapCall(): javax.ws.rs.core.Response
}