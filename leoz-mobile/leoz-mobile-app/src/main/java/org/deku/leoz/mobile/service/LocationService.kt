package org.deku.leoz.mobile.service

import android.app.Service
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
import org.deku.leoz.service.internal.LocationService
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
import sx.time.Duration
import java.util.*

class LocationService(
        val period: Duration,
        val minDistance: Int,
        val allowMockLocation: Boolean,
        val enabled: Boolean
) : Service() {
    private val log = LoggerFactory.getLogger(this.javaClass)
    var locationManager: LocationManager? = null

    constructor() : this(Duration.ofSeconds(30), 250, false, true) {}

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        log.debug("ONSTARTCOMMAND")
        return START_STICKY
    }

    override fun onCreate() {
        log.debug("ONCREATE")
        if (locationManager == null)
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL, DISTANCE, locationListeners[1])
        } catch (e: SecurityException) {
            log.error("Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            log.error("Network provider does not exist", e)
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE, locationListeners[0])
        } catch (e: SecurityException) {
            log.error("Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            log.error("GPS provider does not exist", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            for (i in 0..locationListeners.size) {
                try {
                    locationManager?.removeUpdates(locationListeners[i])
                } catch (e: Exception) {
                    log.warn("Failed to remove location listeners")
                }
            }
    }


    val INTERVAL = period.toMillis() // In milliseconds
    val DISTANCE = minDistance.toFloat() // In meters

    val locationListeners = arrayOf(
            LTRLocationListener(LocationManager.GPS_PROVIDER),
            LTRLocationListener(LocationManager.NETWORK_PROVIDER)
    )

    class LTRLocationListener(provider: String) : android.location.LocationListener {
        private val log = LoggerFactory.getLogger(this.javaClass)
        val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()
        val lastLocation = Location(provider)

        override fun onLocationChanged(location: Location?) {

            log.debug("ONLOCATIONCHANGED")

            if (location == null) {
                log.warn("Location object is null.")
                return
            }

            val currentPosition = LocationService.GpsDataPoint(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    time = Date(location.time),
                    speed = location.speed,
                    bearing = location.bearing,
                    altitude = location.altitude,
                    accuracy = location.accuracy
            )
            lastLocation.set(location)

            log.info("Location changed. Provider [${location.provider}] Position [$currentPosition]")

            // TODO: Store location data in database and send it as an set of multiple positions once.

            // TODO: A list is not a good type for a message. Declasre a dedicated class (eg. PositionMessage) {@link LogMessage}
            mqttChannels.central.transient.channel().send(listOf(currentPosition))
        }

        override fun onProviderDisabled(provider: String?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

    }

}
