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
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.LocationSettings
import org.deku.leoz.mobile.model.process.Login
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
) : Service() {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val locationSettings: LocationSettings by Kodein.global.lazy.instance()

    private var locationManager: LocationManager? = null
    private val locationCache: LocationCache by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()
    private val identity: Identity by Kodein.global.lazy.instance()

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
        if (locationManager == null)
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

//        try {
//            (locationManager as LocationManager).removeTestProvider(LocationManager.GPS_PROVIDER)
//        }catch (e: IllegalArgumentException) {
//
//        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, period.toMillis(), minDistance, locationListeners[1])
        } catch (e: SecurityException) {
            log.error("Fail to request location update", e)
        } catch (e: IllegalArgumentException) {
            log.error("Network provider does not exist", e)
        }

        try {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, period.toMillis(), minDistance, locationListeners[0])
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

    val locationListeners = arrayOf(
            LTRLocationListener(LocationManager.GPS_PROVIDER),
            LTRLocationListener(LocationManager.NETWORK_PROVIDER)
    )

    inner class LTRLocationListener(val provider: String) : android.location.LocationListener {
        private val log = LoggerFactory.getLogger(this.javaClass)
        val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()

        override fun onLocationChanged(location: Location) {
            log.trace("ONLOCATIONCHANGED")

            /**
             * Check weather the acquired location is provided by a mock provider.
             * If so, try to remove TestProviders from this LocationProvider and cancel the processing of this location
             * Only available from API level 18+
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (location.isFromMockProvider && !locationSettings.allowMockLocation) {
                    log.warn("Mock location received!")
                    //Removed because android requires specific permissions to work with mock locations - even removing them
//                try {
//                    if (locationManager == null)
//                        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//                    (locationManager as LocationManager).removeTestProvider(location.provider)
//                } catch (e: Exception) {
//                    log.error("Removing TestProvider from LocationProvider [${location.provider}] failed with an exception.", e)
//                }
                    throw MockLocationException("Received a mock location. Mock locations are not allowed.")
                }
            }

            val currentPosition = LocationServiceV1.GpsDataPoint(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    time = Date(location.time),
                    speed = location.speed,
                    bearing = location.bearing,
                    altitude = location.altitude,
                    accuracy = location.accuracy,
                    vehicleType = login.authenticatedUser?.vehicleType
            )

            this@LocationService.locationCache.lastLocation = location

            log.info("Location changed. Provider [${location.provider}] Position [$currentPosition]")

            // TODO: Store location data in database and send it as an set of multiple positions once.

            mqttChannels.central.transient.channel().send(
                    message = LocationServiceV1.GpsMessage(
                            userId = this@LocationService.login.authenticatedUser?.id,
                            nodeId = this@LocationService.identity.uid.value,
                            dataPoints = arrayOf(currentPosition)
                    )
            )
        }

        override fun onProviderDisabled(provider: String?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }
    }

    class MockLocationException(message: String) : Throwable(message)
}
