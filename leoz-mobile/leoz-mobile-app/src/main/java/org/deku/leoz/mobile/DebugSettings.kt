package org.deku.leoz.mobile

import org.deku.leoz.mobile.BuildConfig
import sx.ConfigurationMap
import sx.ConfigurationMapPath

/**
 * Debug sewttings
 * Created by masc on 31.05.17.
 */
@sx.ConfigurationMapPath("debug")
class DebugSettings(map: sx.ConfigurationMap) {
    val enabled: Boolean by map.value(org.deku.leoz.mobile.BuildConfig.DEBUG)
    val allowDeveloperOptions: Boolean by map.value(enabled)
    val userScreenRotation: Boolean by map.value(false)
}
