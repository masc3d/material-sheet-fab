package org.deku.leoz.mobile

/**
 * Debug sewttings
 * Created by masc on 31.05.17.
 */
@sx.ConfigurationMapPath("debug")
class DebugSettings(map: sx.ConfigurationMap) {
    val enabled: Boolean by map.value(org.deku.leoz.mobile.BuildConfig.DEBUG)
    val allowDeveloperOptions: Boolean by map.value(enabled)
    val userScreenRotation: Boolean by map.value(false)
    val syntheticAidcEnabled: Boolean by map.value(false)

    override fun toString(): String =
            "DebugSettings(enabled=$enabled, allowDeveloperOptions=$allowDeveloperOptions, userScreeRotation=$userScreenRotation, syntheticAidcEnabled=$syntheticAidcEnabled)"
}
