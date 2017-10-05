package org.deku.leoz.mobile.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.LocationServices

/**
 * Created by 27694066 on 05.10.2017.
 */

class LocationProviderChangedReceiver : BroadcastReceiver() {

    private val locationSettings: LocationServices by Kodein.global.lazy.instance()

    override fun onReceive(context: Context, intent: Intent) {
        locationSettings.locationSettingsChangedEventProperty.onNext(Unit)
    }

}