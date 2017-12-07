package org.deku.leoz.mobile.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.receiver.LocationProviderChangedReceiver
import org.deku.leoz.mobile.settings.LocationSettings
import org.deku.leoz.mobile.ui.activity.StartupActivity
import org.deku.leoz.service.internal.LocationServiceV1
import org.deku.leoz.service.internal.LocationServiceV2
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
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

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }
    protected val locationServices: org.deku.leoz.mobile.LocationServices by Kodein.global.lazy.instance()

    private var gnssStatusCallback: GnssStatus.Callback? = null
    @Suppress("DEPRECATION")
    private var gpsStatusListener: GpsStatus.Listener? = null

    private val showTaskIntent by lazy {
        Intent(applicationContext, StartupActivity::class.java).also {
            it.action = Intent.ACTION_VIEW
            it.addCategory(Intent.CATEGORY_LAUNCHER)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private val pendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            showTaskIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val notification by lazy {
        val notificationChannel: NotificationChannel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "mobileX"
            val channelDescription = "mobileX Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH

            notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance).also {
                it.description = channelDescription
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(false)
            }

            this.notificationManager.createNotificationChannel(notificationChannel)
        }



        @Suppress("DEPRECATION")
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)


        builder.setContentTitle(getString(R.string.app_name_long))
                .setContentText("${getString(R.string.app_name)} ${getString(R.string.running)}")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build().also {
            it.flags += Notification.FLAG_FOREGROUND_SERVICE
        }
    }

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "LEOZ"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log.trace("ONSTARTCOMMAND")

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.locationServices.locationManager.registerGnssStatusCallback(gnssStatusCallback)
            } else {
                @Suppress("DEPRECATION")
                this.locationServices.locationManager.addGpsStatusListener(gpsStatusListener)
            }
        }

        this.registerBroadcastReceiver()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log.trace("ONCREATE")

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gnssStatusCallback = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus?) {
                    log.trace("ONSATELLITESTATUSCHANGED")
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
        log.trace("ONDESTROY")
        this.unregisterBroadcastReceiver()
        stopForeground(true)

        super.onDestroy()
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