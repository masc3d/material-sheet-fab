package org.deku.leoz.mobile.settings

/**
 * Location settings
 * Created by phpr on 11.07.2017.
 */
@sx.ConfigurationMapPath("location")
class LocationSettings(map: sx.ConfigurationMap) {
    val enabled: Boolean by map.value(true)
    val minDistance: Int by map.value(500)
    val force: Boolean by map.value(true)
    val period: Long by map.value(120)
    val allowMockLocation: Boolean by map.value(false)
    val useGoogleLocationService: Boolean by map.value(true)
    val smallestDisplacement: Float by map.value(250F)
}