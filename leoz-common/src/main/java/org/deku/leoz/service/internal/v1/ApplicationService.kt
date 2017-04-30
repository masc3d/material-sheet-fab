package org.deku.leoz.service.internal.v1

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.deku.leoz.service.entity.internal.v1.ApplicationVersion
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * Created by masc on 09.10.15.
 */
@javax.ws.rs.Path("internal/v1/application")
@javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@javax.ws.rs.Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
@io.swagger.annotations.Api(value = "Application operations")
interface ApplicationService {
    /**
     * Get entry by name
     * @param name name
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/version")
    @io.swagger.annotations.ApiOperation(value = "Get application version", response = String::class)
    fun getVersion(): org.deku.leoz.service.entity.internal.v1.ApplicationVersion

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/restart")
    @io.swagger.annotations.ApiOperation(value = "Restart application")
    fun restart()

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/bundle-update")
    @io.swagger.annotations.ApiOperation(value = "Trigger application bundle udpates")
    fun bundleUpdate()

    @javax.ws.rs.GET
    @javax.ws.rs.Path("/notify-bundle-update")
    @io.swagger.annotations.ApiOperation(value = "Notify remote nodes about bundle update")
    fun notifyBundleUpdate(@io.swagger.annotations.ApiParam(value = "Bundle name") @javax.ws.rs.QueryParam("bundle-name") bundleName: String)
}