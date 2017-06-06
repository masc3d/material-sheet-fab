package org.deku.leoz.service.internal.entity

import io.swagger.annotations.*

/**
 * Created by 27694066 on 25.04.2017.
 */
@ApiModel(description = "User Model")
data class User(
        @get:ApiModelProperty(example = "foo@bar.com", required = true, value = "User identifier")
        var email: String = "@",

        @get:ApiModelProperty(example = "12345678", required = false, value = "Allocation of User to debitor")
        var debitorId: Int? = null,

        @get:ApiModelProperty(example = "foo.bar", required = false, value = "Alias of the user")
        var alias: String? = null,

        @get:ApiModelProperty(example = "User", required = true, value = "Role of the user", allowableValues = "Admin, PowerUser, User, Driver, Custom")
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

        @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = false, value = "Date this account is supposed to expire")
        var expiresOn: java.sql.Date? = null
)

enum class UserRole(val value: String) {
    Admin("admin"),
    PowerUser("power-user"),
    User("user"),
    Driver("driver"),
    Customer("customer")
}

val User.isActive: Int
    get() = if (this.active == null || this.active == false) 0 else -1

val User.isExternalUser: Int
    get() = if (this.externalUser == null || this.externalUser == false) 0 else -1

const val HEADERPARAM_APIKEY = "x-api-key"