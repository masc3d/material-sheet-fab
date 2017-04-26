package org.deku.leoz.mobile.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import org.slf4j.LoggerFactory
import sx.time.Duration

class LocationService(
        val period: Duration,
        val minDistance: Int,
        val allowMockLocation: Boolean,
        val enabled: Boolean
) : Service()
{
    private val log = LoggerFactory.getLogger(this.javaClass)
    var locationManager: LocationManager? = null

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }



    override fun onCreate() {
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

            val lastLocation = Location(provider)

            override fun onLocationChanged(location: Location?) {
                lastLocation.set(location)
                // TODO: Do something here
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

        }

}
