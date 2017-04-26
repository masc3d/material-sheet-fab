package org.deku.leoz.rest.entity.internal.v1

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.sql.Date

/**
 * Created by 27694066 on 25.04.2017.
 */
@ApiModel(description = "User Model")
data class User (

    @get:ApiModelProperty(example = "foo@bar.com", required = true, value = "User identifier")
    @get:JsonProperty("email")
    var email: String? = null,

    @get:ApiModelProperty(example = "12345678", required = false, value = "Allocation of User to debitor")
    @get:JsonProperty("debitorId")
    var debitorId: Int? = null,

    @get:ApiModelProperty(required = false, value = "Allocation of User to several station ids")
    @get:JsonProperty("stations")
    var stations: List<String>? = null,

    @get:ApiModelProperty(example = "foo.bar", required = true, value = "Alias of the user")
    @get:JsonProperty("alias")
    var alias: String? = null,

    @get:ApiModelProperty(example = ROLE_USER, required = true, value = "Role of the user", allowableValues = "$ROLE_ADMINISTRATOR,$ROLE_POWERUSER,$ROLE_USER,$ROLE_DRIVER,$ROLE_CUSTOMER")
    @get:JsonProperty("role")
    var role: String? = null,

    // TODO: should be hash instead of pw
    @get:ApiModelProperty(example = "MyS3cr3t", required = true, value = "Password")
    @get:JsonProperty("password")
    var password: String? = null,

    @get:ApiModelProperty(example = "Foo", required = true, value = "First name")
    @get:JsonProperty("firstName")
    var firstName: String? = null,

    @get:ApiModelProperty(example = "Bar", required = true, value = "Last name")
    @get:JsonProperty("lastName")
    var lastName: String? = null,

    @get:ApiModelProperty(example = "1a-2b-3c-4d-5e-6f", required = false, value = "API Key")
    @get:JsonProperty("apiKey")
    var apiKey: String? = null,

    @get:ApiModelProperty(example = "true", required = false, value = "Active user")
    @get:JsonProperty("active")
    var active: Boolean? = null,

    @get:ApiModelProperty(example = "true", required = false, value = "External user")
    @get:JsonProperty("externalUser")
    var externalUser: Boolean? = null,

    @get:ApiModelProperty(example = "+496677950", required = true, value = "Phone number")
    @get:JsonProperty("phone")
    var phone: String? = null,

    @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = false, value = "Date this account is supposed to expire")
    @get:JsonProperty("expiresOn")
    var expiresOn: Date? = null
)
{
    companion object {
        const val ROLE_ADMINISTRATOR: String = "ADMINISTRATOR"
        const val ROLE_POWERUSER: String = "POWERUSER"
        const val ROLE_USER: String = "USER"
        const val ROLE_DRIVER: String = "DRIVER"
        const val ROLE_CUSTOMER: String = "CUSTOMER"
    }
}