package org.deku.leoz.mobile.service

import android.content.Context
import android.content.Intent
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

class LocationService(
        var period: Duration = Duration.ofSeconds(30),
        var minDistance: Float = 250F,
        var enabled: Boolean = true
)
    : BaseLocationService() {

    private var locationListener: LocationListener? = null

    init {
        this.period = Duration.ofSeconds(locationSettings.period)
        this.minDistance = locationSettings.minDistance.toFloat()
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
            locationManager.requestLocationUpdates(provider, period.toMillis(), minDistance, locationListener)
        } catch (e: SecurityException) {
            log.error("Fail to request location update on provider [$provider]", e)
        } catch (e: IllegalArgumentException) {
            log.error("Provider [$provider] does not exist", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
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
