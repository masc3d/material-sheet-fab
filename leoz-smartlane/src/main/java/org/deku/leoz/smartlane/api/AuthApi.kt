package org.deku.leoz.smartlane.api

import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/")
interface AuthApi {

    data class Request(
            @ApiModelProperty(value = "Email")
            var email: String = "",
            @ApiModelProperty(value = "Password")
            var password: String = ""
    )

    data class Response(
            @ApiModelProperty(value = "Access token")
            var accessToken: String = ""
    )

    /**
     * Created by masc on 16.11.17.
     */
    @POST
    @Path("/auth")
    @Consumes("application/json")
    @Produces("application/json")
    fun auth(@Valid body: Request): Response
}
