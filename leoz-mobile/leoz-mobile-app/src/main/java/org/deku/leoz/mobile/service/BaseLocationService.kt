package org.deku.leoz.mobile.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Notifications
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.receiver.LocationProviderChangedReceiver
import org.deku.leoz.mobile.settings.LocationSettings
import org.deku.leoz.service.internal.LocationServiceV1
import org.deku.leoz.service.internal.LocationServiceV2
import org.slf4j.LoggerFactory
import sx.android.NtpTime
import sx.mq.mqtt.channel
import sx.time.TimeSpan
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by 27694066 on 22.09.2017.
 */
abstract class BaseLocationService: Service() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val identity: Identity by Kodein.global.lazy.instance()
    private val locationCache: LocationCache by Kodein.global.lazy.instance()
    protected val locationSettings: LocationSettings by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()
    private val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()
    private val locationProviderChangedReceiver: LocationProviderChangedReceiver by Kodein.global.lazy.instance()

    private val notifications: Notifications by Kodein.global.lazy.instance()
    private val ntpTime: NtpTime by Kodein.global.lazy.instance()
    protected val locationServices: org.deku.leoz.mobile.LocationServices by Kodein.global.lazy.instance()

    private var gnssStatusCallback: GnssStatus.Callback? = null
    @Suppress("DEPRECATION")
    private var gpsStatusListener: GpsStatus.Listener? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log.debug("ONSTARTCOMMAND")

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.locationServices.locationManager.registerGnssStatusCallback(gnssStatusCallback)
            } else {
                @Suppress("DEPRECATION")
                this.locationServices.locationManager.addGpsStatusListener(gpsStatusListener)
            }
        }

        this.registerBroadcastReceiver()
        setNotification()
        startForeground(notifications.serviceNotification)
        //startForeground(notifications.serviceNotification.id, notifications.serviceNotification.notification)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log.debug("ONCREATE")

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gnssStatusCallback = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus?) {
                    log.debug("ONSATELLITESTATUSCHANGED")
                    locationServices.locationSettingsChangedEventProperty.onNext(Unit)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            gpsStatusListener = GpsStatus.Listener {
                when (it) {
                    GpsStatus.GPS_EVENT_STOPPED -> locationServices.locationSettingsChangedEventProperty.onNext(Unit)
                }
            }
        }
    }

    override fun onDestroy() {
        log.debug("ONDESTROY")
        this.unregisterBroadcastReceiver()
        removeNotification()
        stopForeground(true)

        super.onDestroy()
    }

    private fun setNotification() {
        log.debug("Set notification")
        notifications.serviceNotification.show()
    }

    private fun removeNotification() {
        log.debug("Remove notification")
        notifications.serviceNotification.cancel()
    }

    fun reportLocation(location: Location) {
        log.trace("New location reported")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (location.isFromMockProvider && !locationSettings.allowMockLocation) {
                log.warn("Mock location received!")
                throw LocationService.MockLocationException("Received a mock location. Mock locations are not allowed.")
            }
        }

        if (!locationRequirementsCheck(locationNew = location, locationOld = this@BaseLocationService.locationCache.lastLocation)) {
            log.debug("Location does not match requirements.")
            return
        }

        /**
         * This is to ensure that no "corrupt" time is emitted in the GPSMessage, which is likely to happen because of an Android Bug
         * See Issue #247 for more information
         * If the location time differs more than ~15 minutes from the real/device time, the real/device time is used
         */
        val date = ntpTime.time ?: Date()
        if ((date.time - location.time) > 1000000) {
            log.warn("The location timestamp [${location.time}] seems to be unreliable. Using [${date.time}] instead. Difference in minutes [${TimeSpan(date.time - location.time).totalMinutes}]")
            location.time = date.time
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

        this@BaseLocationService.locationCache.lastLocation = location

        log.info("Location changed. Provider [${location.provider}] Position [$currentPosition]")

        // TODO: Store location data in database and send it as an set of multiple positions once.

        mqttChannels.central.transient.channel().send(
                message = LocationServiceV2.GpsMessage(
                        userId = this@BaseLocationService.login.authenticatedUser?.id,
                        nodeKey = this@BaseLocationService.identity.uid.value,
                        dataPoints = arrayOf(currentPosition)
                )
        )
    }

    private fun registerBroadcastReceiver() {
        val broadcastManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter(Intent.ACTION_PROVIDER_CHANGED)

        log.debug("Registering BroadcastReceiver [${locationProviderChangedReceiver::class.java.simpleName}] IntentFilter [$intentFilter]")
        broadcastManager.registerReceiver(locationProviderChangedReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        val broadcastManager = LocalBroadcastManager.getInstance(this)

        log.debug("Unregister BroadcastReceiver [${locationProviderChangedReceiver::class.java.simpleName}]")
        broadcastManager.unregisterReceiver(locationProviderChangedReceiver)
    }

    private fun locationRequirementsCheck(locationNew: Location, locationOld: Location?): Boolean {
        if (locationOld == null)
            return true

        if (locationNew.provider == LocationManager.GPS_PROVIDER || locationOld.accuracy >= locationNew.accuracy)
            return true

        val baseValue = 50
        val positionAge = TimeUnit.MILLISECONDS.toMinutes(locationNew.time - locationOld.time)
        val allowedDistance = (baseValue * (positionAge * 0.15))

        if ((locationOld.accuracy + allowedDistance) >= locationNew.accuracy)
            return true

        //Earth radius in meters
        if (locationNew.distanceTo(locationOld) <= allowedDistance)
            return true

        if (positionAge > 5 && locationOld.bearingTo(locationNew) in (locationOld.bearing - 10F)..(locationOld.bearing + 10F))
            return true

        return false
    }

}