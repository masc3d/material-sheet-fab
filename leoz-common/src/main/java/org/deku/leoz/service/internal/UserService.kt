package org.deku.leoz.service.internal

import io.swagger.annotations.*
import org.deku.leoz.config.Rest
import org.deku.leoz.model.UserPreferenceKey
import sx.io.serialization.Serializable
import sx.rs.auth.ApiKey
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * User service
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "User operations", authorizations = arrayOf(Authorization(Rest.API_KEY)))
@ApiKey
interface UserService {
    companion object {
        //const val ID = "id"
        const val USER_ID = "user-id"
        const val EMAIL = "email"
        const val DEBITOR_ID = "debitor-id"
        const val PREF_TYPE = "preference-type"
        const val SEND_APP_LINK_SMS = "send-app-sms"
        const val STATION_MATCHCODE = "station-matchcode"
        const val DEBITOR_NR = "debitor-nr"
        //const val DEBITOR_NO = "debitor-no"
        //const val Rest.AUTH_APIKEY_NAME = "x-api-key"
        const val OLD_PASSWORD="old-password"
        const val NEW_PASSWORD="new-password"
    }

    /**
     * Created by 27694066 on 25.04.2017.
     */
    @ApiModel(description = "User Model")
    @Serializable(0x4b4e300fc8dec6)
    data class User(
            @get:ApiModelProperty(value = "User id")
            var id: Int? = null,

            @get:ApiModelProperty(example = "foo@bar.com", required = true, value = "User email")
            var email: String = "@",

            @get:ApiModelProperty(example = "12345678", required = false, value = "Allocation of User to debitor")
            var debitorId: Int? = null,

            @get:ApiModelProperty(example = "foo.bar", required = false, value = "Alias of the user")
            var alias: String? = null,

            @get:ApiModelProperty(example = "User", required = true, value = "Role of the user", allowableValues = "Admin, PowerUser, User, Driver, Customer")
            var role: String? = null,

            // TODO: should be hash instead of pw
            @get:ApiModelProperty(example = "MyS3cr3t", required = false, value = "Password")
            var password: String? = null,
            @get:ApiModelProperty(example = "Foo", required = false, value = "First name")
            var firstName: String? = null,

            @get:ApiModelProperty(example = "Bar", required = false, value = "Last name")
            var lastName: String? = null,

            @get:ApiModelProperty(example = "true", required = false, value = "Active user")
            var active: Boolean? = null,

            @get:ApiModelProperty(example = "true", required = false, value = "External user")
            var externalUser: Boolean? = null,

            @get:ApiModelProperty(example = "+496677950", required = false, value = "Phone number")
            var phone: String? = null,

            @get:ApiModelProperty(example = "+491713456789", required = false, value = "Mobile phone number")
            var phoneMobile: String? = null,

            @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = false, value = "Date this account is supposed to expire")
            var expiresOn: java.sql.Date? = null,

            //@get:ApiModelProperty(example = "220,221",dataType = "String[]", required = false, value = "allowed stations")
            //@get:ApiModelProperty(example = "220,221",dataType = "List", required = false, value = "allowed stations")
            //@get:ApiModelProperty(example = "220,221",dataType = "java.util.List<String>", required = false, value = "allowed stations")
            //@get:ApiModelProperty(example = "[\"220\",\"221\"]", required = false, value = "allowed stations")
            //@get:ApiModelProperty(example = ["220","221"], required = false, value = "allowed stations")
            //@get:ApiModelProperty(example = "220,221", dataType = "Array<String>", required = false, value = "allowed stations")
            //@get:ApiModelProperty(example = "[220,221]", required = false, value = "allowed stations")
            @get:ApiModelProperty(example = "[220,221]", required = false, value = "allowed stations")
            var allowedStations: List<Int>? = null,

            @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = false, value = "Date this accounts password is supposed to expire")
            var passwordExpiresOn: java.sql.Date? = null

//            @get:ApiModelProperty(example = "", required = false, value = "config")
//            var config: String? = null,
//            @get:ApiModelProperty(example = "", required = false, value = "preferences")
//            var preferences: Preferences? = null
    )

    @ApiModel(description = "User preference object")
    data class Preferences(
            val type: Type,
            val prefs: Map<UserPreferenceKey, Any>
    ) {
        enum class Type(val value: String) {
            WEB_UI("WEB_UI_PREFERENCE"),
            MOBILE("MOBILE_PREFERENCE")
        }
    }

    @Serializable(0x226a3e74f8e3)
    data class DataProtectionActivity(
            val scope: DataProtectionActivity.Scope,
            val userId: Int,
            val ts_activity: Date,
            val confirmed: Boolean,
            val policyVersion: Int
    ) {
        enum class Scope {
            MOBILE
        }
    }

    val User.isActive: Int
        get() = if (this.active == null || this.active == false) 0 else -1

    val User.isExternalUser: Int
        get() = if (this.externalUser == null || this.externalUser == false) 0 else -1

    /**
     * Get user
     * @param email User email
     */
    @GET
    @Path("/")
    @ApiOperation(value = "Get user")
    fun get(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null
    ): List<User>

    @GET
    @Path("/{$USER_ID}")
    @ApiOperation(value = "Get user by ID")
    fun getById(
            @PathParam(USER_ID) @ApiParam(value = "Users identifier") userId: Int
    ): User

    /**
     * Create user
     * @param user User to create
     */
    @POST
    @Path("/")
    @ApiOperation(value = "Create user")
    fun create(
            @ApiParam(value = "User") user: User,
            @QueryParam(value = STATION_MATCHCODE) @ApiParam(value = "Station Matchcode", example = "011", required = false) stationMatchcode: String? = null,
            @QueryParam(value = DEBITOR_NR) @ApiParam(value = "Debitor No.", example = "12345678", required = false) debitorNr: Long? = null,
            @QueryParam(value = SEND_APP_LINK_SMS) @ApiParam(value = "Send App link via SMS to the User?", required = false, defaultValue = "true") @DefaultValue("true") sendAppLink: Boolean = true
    )

    /**
     * Update user (replaces entire user)
     * @param id Id of user to update
     * @param user User entity
     */
    @PUT
    @Path("/")
    @ApiOperation(value = "Update user")
    fun update(@QueryParam(EMAIL) @ApiParam(value = "User email address") email: String,
               @ApiParam(value = "User") user: User,
               @QueryParam(value = SEND_APP_LINK_SMS) @ApiParam(value = "Send App link via SMS to the User?", required = false, defaultValue = "false", hidden = true) @DefaultValue("false") sendAppLink: Boolean = false)

    @POST
    @Path("/{$USER_ID}/sendAppLink")
    @ApiOperation(value = "Send App download-link")
    fun sendDownloadLink(@PathParam(USER_ID) @ApiParam(value = "Users identifier") userId: Int): Boolean

    @PATCH
    @Path("/{$USER_ID}/changePassword")
    @ApiOperation(value = "Change users password")
    fun changePassword(
            @PathParam(value = USER_ID) @ApiParam(value = "Users identifier", required = true) userId: Int,
            @QueryParam(value = OLD_PASSWORD) @ApiParam(value = "old_password", required = true) oldPassword: String,
            @QueryParam(value = NEW_PASSWORD) @ApiParam(value = "new_password", required = true) newPassword: String
    )

    @GET
    @Path("/{$USER_ID}/configuration")
    @ApiOperation(value = "Get user configuration by user-id")
    fun getConfigurationById(
            @PathParam(value = USER_ID) userId: Int
    ): String

    @GET
    @Path("/auth/configuration")
    @ApiOperation(value = "Get user configuration for current user")
    fun getCurrentUserConfiguration(): String

    @GET
    @Path("/id")
    @ApiOperation(value = "Get user id(s) by debitor id")
    fun getIdsByDebitor(@QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id",required = true) debitorId: Int): List<Int>
}