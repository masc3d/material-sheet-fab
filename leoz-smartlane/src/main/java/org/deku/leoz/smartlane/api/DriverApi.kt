package org.deku.leoz.smartlane.api

import org.deku.leoz.smartlane.model.Driver
import sx.rs.FlaskOperator
import sx.rs.FlaskQuery

/**
 * Smartlane driver api extensions
 * Created by masc on 02.02.18.
 */

/**
 * Get driver by email
 * @param email Email
 */
fun DriverApi.getDriverByEmail(email: String): Driver? {
    return this.getDriver(sx.rs.FlaskFilter(
            FlaskQuery(
                    name = "email",
                    op = FlaskOperator.EQ,
                    value = email)
    ).toJson(),
            true,
            true,
            true
    ).objects.firstOrNull()
}