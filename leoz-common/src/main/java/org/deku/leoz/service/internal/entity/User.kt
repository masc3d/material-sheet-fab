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

        //        @get:ApiModelProperty(required = false, value = "Allocation of User to several station ids")
//        var stations: List<String>? = null,

        @get:ApiModelProperty(example = "foo.bar", required = true, value = "Alias of the user")
        var alias: String? = null,

        @get:ApiModelProperty(example = org.deku.leoz.service.internal.entity.User.Companion.ROLE_USER, required = true, value = "Role of the user", allowableValues = "${org.deku.leoz.service.internal.entity.User.Companion.ROLE_ADMINISTRATOR},${org.deku.leoz.service.internal.entity.User.Companion.ROLE_POWERUSER},${org.deku.leoz.service.internal.entity.User.Companion.ROLE_USER},${org.deku.leoz.service.internal.entity.User.Companion.ROLE_DRIVER},${org.deku.leoz.service.internal.entity.User.Companion.ROLE_CUSTOMER}")
        var role: String? = null,

        // TODO: should be hash instead of pw
        @get:ApiModelProperty(example = "MyS3cr3t", required = true, value = "Password")
        var password: String? = null,

        //        @get:ApiModelProperty(example = "a1b2c3d4e5f6", required = true, value = "Salt")
//        var salt: String? = null,

        @get:ApiModelProperty(example = "Foo", required = true, value = "First name")
        var firstName: String? = null,

        @get:ApiModelProperty(example = "Bar", required = true, value = "Last name")
        var lastName: String? = null,

        //        @get:ApiModelProperty(example = "1a-2b-3c-4d-5e-6f", required = false, value = "API Key")
//        var apiKey: String? = null,

        @get:ApiModelProperty(example = "true", required = false, value = "Active user")
        var active: Boolean? = null,

        @get:ApiModelProperty(example = "true", required = false, value = "External user")
        var externalUser: Boolean? = null,

        @get:ApiModelProperty(example = "+496677950", required = true, value = "Phone number")
        var phone: String? = null,

        @get:ApiModelProperty(example = "2017-03-16T17:00:00.000Z", required = false, value = "Date this account is supposed to expire")
        var expiresOn: java.sql.Date? = null

        //var id: Int = 0
) {
    companion object {
        const val ROLE_ADMINISTRATOR: String = "ADMIN"
        const val ROLE_POWERUSER: String = "POWERUSER"
        const val ROLE_USER: String = "USER"
        const val ROLE_DRIVER: String = "DRIVER"
        const val ROLE_CUSTOMER: String = "CUSTOMER"
    }
}

enum class UserRole(val value: Int) {
    ADMINISTRATOR(10),
    POWERUSER(7),
    USER(6),
    DRIVER(4),
    CUSTOMER(2)
}

val User.isActive: Int
    get() = if (this.active == null || this.active == false) 0 else -1

val User.isExternalUser: Int
    get() = if (this.externalUser == null || this.externalUser == false) 0 else -1