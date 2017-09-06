package org.deku.leoz.service.internal

import org.deku.leoz.service.internal.entity.HEADERPARAM_APIKEY
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import io.swagger.annotations.*
import org.deku.leoz.model.UserPreferenceKey

/**
 * User service
 * Created by masc on 09.10.15.
 */
@Path("internal/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "User operations")
interface UserService {
    companion object {
        //const val ID = "id"
        const val USER_ID = "user-id"
        const val EMAIL = "email"
        const val DEBITOR_ID = "debitor-id"
        const val PREF_TYPE = "preference-type"
        const val SEND_CRED = "send-credentials"
        //const val DEBITOR_NO = "debitor-no"
        //const val HEADERPARAM_APIKEY = "x-api-key"
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
            var expiresOn: java.sql.Date? = null
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
    @ApiOperation(value = "Get user")
    fun get(
            @QueryParam(EMAIL) @ApiParam(value = "User email address") email: String? = null,
            @QueryParam(DEBITOR_ID) @ApiParam(value = "Debitor id") debitorId: Int? = null,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
    ): List<User>

    @GET
    @Path("/{$USER_ID}")
    @ApiOperation(value = "Get user by ID")
    fun getById(
            @QueryParam(USER_ID) @ApiParam(value = "Users identifier") userId: Int,
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?
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
            @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?,
            @QueryParam(value = SEND_CRED) @ApiParam(value = "Send credentials to newly created user?", required = false, defaultValue = "true") sendAppLink: Boolean = true
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
               @HeaderParam(HEADERPARAM_APIKEY) @ApiParam(hidden = true) apiKey: String?)

    @POST
    @Path("/{$USER_ID}/sendAppLink")
    @ApiOperation(value = "Send App download-link")
    fun sendDownloadLink(@PathParam(USER_ID) @ApiParam(value = "Users identifier") userId: Int): Boolean
}