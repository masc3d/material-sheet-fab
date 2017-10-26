package org.deku.leoz.mobile.settings

import org.threeten.bp.Duration

/**
 * User settings
 * Created by phpr on 11.07.2017.
 */
@sx.ConfigurationMapPath("user")
class UserSettings(map: sx.ConfigurationMap) {
    val idleTimeoutMinutes: Int by map.value(240)

    val idleTimeout by lazy { Duration.ofMinutes(this.idleTimeoutMinutes.toLong()) }
}