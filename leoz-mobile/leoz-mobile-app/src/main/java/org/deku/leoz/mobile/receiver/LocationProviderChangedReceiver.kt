package org.deku.leoz.mobile.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.service.LocationServices
import org.slf4j.LoggerFactory

/**
 * Created by 27694066 on 05.10.2017.
 */

class LocationProviderChangedReceiver : BroadcastReceiver() {

    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    private val locationSettings: LocationServices by Kodein.global.lazy.instance()

    override fun onReceive(context: Context, intent: Intent) {
        log.debug("BroadcastReceiver [${this::class.java.simpleName}] fired")
        log.debug("ONRECEIVE Intent [${intent.action}] Context [$context]")
        Log.d(LocationProviderChangedReceiver::class.java.simpleName, "ONRECEIVE")
        locationSettings.locationSettingsChangedEventSubject.onNext(Unit)
    }

}