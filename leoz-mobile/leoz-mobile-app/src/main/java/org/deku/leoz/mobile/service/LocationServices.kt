package org.deku.leoz.mobile.service

import android.content.Context
import android.location.LocationManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import io.reactivex.subjects.PublishSubject
import org.deku.leoz.mobile.settings.LocationSettings
import org.threeten.bp.Duration

/**
 * Created by 27694066 on 25.09.2017.
 */
class LocationServices(val context: Context) {

    val locationSettings: LocationSettings by Kodein.global.lazy.instance()
    val locationManager: LocationManager by lazy {
        (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    }

    val locationSettingsChangedEventSubject = PublishSubject.create<Unit>()
    val locationSettingsChangedEvent = locationSettingsChangedEventSubject.hide()

    val locationRequest = LocationRequest().also {
        it.interval = Duration.ofSeconds(this.locationSettings.period).toMillis()
        it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        it.smallestDisplacement = locationSettings.smallestDisplacement
    }
}