package org.deku.leoz.service.internal

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.Authorization
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserRole
import org.deku.leoz.rest.RestrictRoles
import org.deku.leoz.time.ShortDate
import sx.rs.auth.ApiKey
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Recovery service
 * Created by masc on 25.05.18.
 */
@Path("internal/v1/recover")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Recovery operations", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
@RestrictRoles(UserRole.ADMIN)
interface RecoveryService {
    companion object {
        const val DRY_RUN = "dry-run"
        const val LOG_DATE = "log-date"
        const val APPLICATION_VERSION = "application-update-version"
    }

    /**
     * Dump stations
     */
    @GET
    @Path("/mobile/parcel-messages")
    @ApiOperation(value = "Recover parcel messages from mobile logs")
    fun recoverMobileParcelMessages(
            @QueryParam(DRY_RUN) @DefaultValue("true") dryRun: Boolean,
            @QueryParam(LOG_DATE) @DefaultValue("") @ApiParam(value = "Date of the logfile", example = "2018-05-25") logDate: ShortDate?,
            @QueryParam(APPLICATION_VERSION) applicationUpdateVersion: String?
    )
}
