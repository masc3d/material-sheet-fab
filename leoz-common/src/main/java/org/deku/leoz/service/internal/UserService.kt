package org.deku.leoz.service.internal

import org.deku.leoz.config.Rest
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.model.UserPreferenceKey
import sx.rs.PATCH
import sx.rs.auth.ApiKey

/**
 * User service
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "User operations")
@ApiKey(true)
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
    }

    /**
     * Created by 27694066 on 25.04.2017.
     */
    @ApiModel(description = "User Model")
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
    @ApiOperation(value = "Get user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?
    ): List<User>

    @GET
    @Path("/{$USER_ID}")
    @ApiOperation(value = "Get user by ID", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun getById(
            @PathParam(USER_ID) @ApiParam(value = "Users identifier") userId: Int,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?
    ): User

    /**
     * Create user
     * @param user User to create
     */
    @POST
    @Path("/")
    @ApiOperation(value = "Create user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun create(
            @ApiParam(value = "User") user: User,
            @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?,
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
    @ApiOperation(value = "Update user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun update(@QueryParam(EMAIL) @ApiParam(value = "User email address") email: String,
               @ApiParam(value = "User") user: User,
               @HeaderParam(Rest.API_KEY) @ApiParam(hidden = true) apiKey: String?,
               @QueryParam(value = SEND_APP_LINK_SMS) @ApiParam(value = "Send App link via SMS to the User?", required = false, defaultValue = "false", hidden = true) @DefaultValue("false") sendAppLink: Boolean = false)

    @POST
    @Path("/{$USER_ID}/sendAppLink")
    @ApiOperation(value = "Send App download-link", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun sendDownloadLink(@PathParam(USER_ID) @ApiParam(value = "Users identifier") userId: Int): Boolean

    @PATCH
    @Path("/{$USER_ID}/changePassword")
    @ApiOperation(value = "Change users password", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun changePassword(
            @ApiParam(value = "old_password") oldPassword: String,
            @ApiParam(value = "new_password") newPassword: String
    )

    @GET
    @Path("/auth")
    @ApiOperation(value = "Get auth user", authorizations = arrayOf(Authorization(Rest.API_KEY)))
    fun get(): User
}