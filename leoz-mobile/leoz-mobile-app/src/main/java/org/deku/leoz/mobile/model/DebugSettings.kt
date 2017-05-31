package org.deku.leoz.mobile.model

import org.deku.leoz.mobile.BuildConfig
import sx.ConfigurationMap
import sx.ConfigurationMapPath

/**
 * Debug sewttings
 * Created by masc on 31.05.17.
 */
@ConfigurationMapPath("debug")
class DebugSettings(map: ConfigurationMap) {
    val enabled: Boolean by map.value(BuildConfig.DEBUG)
}
