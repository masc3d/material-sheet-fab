package org.deku.leoz.mobile.settings

/**
 * Tour settings block
 * Created by masc on 8.2.2018
 */
@sx.ConfigurationMapPath("tour")
class TourSettings(private val map: sx.ConfigurationMap) {

    @sx.ConfigurationMapPath("tour.optimization")
    inner class Optimization {
        val enabled: Boolean by map.value(false)
    }

    val optimization = Optimization()
}