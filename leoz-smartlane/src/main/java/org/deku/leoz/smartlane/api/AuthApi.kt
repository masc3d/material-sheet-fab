package org.deku.leoz.smartlane.api

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import javax.validation.Valid
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces

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
    @ApiOperation(value = "Auth", tags = arrayOf("Authorization"))
    @ApiResponses(value = *arrayOf(
            ApiResponse(code = 200, message = "OK", response = Response::class),
            ApiResponse(code = 403, message = "A failure message caused by missing authorization (403 forbidden)", response = String::class),
            ApiResponse(code = 422, message = "A failure message caused by unprocessable input (e.g. no data found for input parameters)", response = String::class)))
    fun auth(@Valid body: Request): Response
}
