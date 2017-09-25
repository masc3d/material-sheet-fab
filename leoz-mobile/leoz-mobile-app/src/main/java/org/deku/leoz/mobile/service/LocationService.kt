package org.deku.leoz.mobile.service

import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.service.internal.LocationServiceV1
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration
import sx.mq.mqtt.channel
import java.util.*

class LocationService
    : BaseLocationService() {

    private var locationListener: LocationListener? = null
    val period = Duration.ofSeconds(locationSettings.period).toMillis()
    val minDistance = locationSettings.minDistance.toFloat()

    private val defaultCriteria by lazy {
        Criteria().also {
            it.powerRequirement = Criteria.POWER_MEDIUM
            it.accuracy = Criteria.ACCURACY_FINE
            it.isSpeedRequired = true
            it.isAltitudeRequired = false
            it.isAltitudeRequired = false
            it.isBearingRequired = true
            it.isCostAllowed = true
        }
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        log.trace("ONSTARTCOMMAND")

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val provider = getProviderName()
        locationListener = LocationListener()

        try {
            locationManager.requestLocationUpdates(provider, period, minDistance, locationListener)
        } catch (e: SecurityException) {
            log.error("Fail to request location update on provider [$provider]", e)
        } catch (e: IllegalArgumentException) {
            log.error("Provider [$provider] does not exist", e)
        }
    }

    override fun onDestroy() {
        locationManager.removeUpdates(locationListener)
        super.onDestroy()
    }

    private fun getProviderName(criteria: Criteria = defaultCriteria): String {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.getBestProvider(criteria, false)
    }

    inner class LocationListener : android.location.LocationListener {
        private val log = LoggerFactory.getLogger(this.javaClass)

        override fun onLocationChanged(location: Location) {
            this.log.trace("ONLOCATIONCHANGED")
            reportLocation(location)
        }

        override fun onProviderDisabled(provider: String?) {
            log.trace("ONPROVIDERDISABLED [$provider]")
        }

        override fun onProviderEnabled(provider: String?) {
            log.trace("ONPROVIDERENABLED [$provider]")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            log.trace("ONSTATUSCHANGED [$provider] Status [$status]")
        }
    }

    class MockLocationException(message: String): Throwable(message)
}
