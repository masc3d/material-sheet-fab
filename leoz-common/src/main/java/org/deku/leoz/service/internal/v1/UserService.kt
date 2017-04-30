package org.deku.leoz.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.service.entity.internal.v1.User
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * User service
 * Created by masc on 09.10.15.
 */
@javax.ws.rs.Path("internal/v1/user")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "User operations")
interface UserService {
    companion object {
        const val EMAIL = "email"
    }

    /**
     * Get user
     * @param email User email
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    @io.swagger.annotations.ApiOperation(value = "Get user", response = String::class)
    fun get(@javax.ws.rs.QueryParam(EMAIL) @io.swagger.annotations.ApiParam(value = "User email address") email: String? = null): org.deku.leoz.service.entity.internal.v1.User
}