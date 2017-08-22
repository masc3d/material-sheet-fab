package org.deku.leoz.mobile.service

import android.location.Location
import sx.rx.ObservableRxProperty

/**
 * Shallow injectable location cache.
 * Primarily introduced becauase implementing injectable android service singletons is cumbersome
 *
 * Created by masc on 10.08.17.
 */
class LocationCache {
    val lastLocationProperty = ObservableRxProperty<Location?>(null)
    var lastLocation by lastLocationProperty

}