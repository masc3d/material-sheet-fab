package org.deku.leoz.mobile.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.LocationSettings
import org.deku.leoz.mobile.R
import org.deku.leoz.mobile.model.process.Login
import org.deku.leoz.mobile.mq.MqttEndpoints
import org.deku.leoz.mobile.ui.activity.StartupActivity
import org.deku.leoz.service.internal.LocationServiceV1
import org.slf4j.LoggerFactory
import sx.mq.mqtt.channel
import java.util.*

/**
 * Created by 27694066 on 22.09.2017.
 */
abstract class BaseLocationService: Service() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val identity: Identity by Kodein.global.lazy.instance()
    private val locationCache: LocationCache by Kodein.global.lazy.instance()
    protected val locationManager: LocationManager by lazy {
        applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    protected val locationSettings: LocationSettings by Kodein.global.lazy.instance()
    private val login: Login by Kodein.global.lazy.instance()
    private val mqttChannels: MqttEndpoints by Kodein.global.lazy.instance()

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

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
        Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name_long))
                .setContentText("${getString(R.string.app_name)} ${getString(R.string.running)}")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build().also {
            it.flags += Notification.FLAG_FOREGROUND_SERVICE
        }
    }

    companion object {
        const val NOTIFICATION_ID = 100
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //return super.onStartCommand(intent, flags, startId)
        log.debug("ONSTARTCOMMAND")
        //setNotification()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        log.debug("ONCREATE")
        setNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        log.debug("ONDESTROY")
        //removeNotification()
        removeNotification()
        stopForeground(true)
    }

    private fun setNotification() {
        log.debug("Set notification")
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun removeNotification() {
        log.debug("Remove notification")
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun reportLocation(location: Location) {
        log.trace("New location reported")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (location.isFromMockProvider && !locationSettings.allowMockLocation) {
                log.warn("Mock location received!")
                throw LocationService.MockLocationException("Received a mock location. Mock locations are not allowed.")
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

        this@BaseLocationService.locationCache.lastLocation = location

        log.info("Location changed. Provider [${location.provider}] Position [$currentPosition]")

        // TODO: Store location data in database and send it as an set of multiple positions once.

        mqttChannels.central.transient.channel().send(
                message = LocationServiceV1.GpsMessage(
                        userId = this@BaseLocationService.login.authenticatedUser?.id,
                        nodeId = this@BaseLocationService.identity.uid.value,
                        dataPoints = arrayOf(currentPosition)
                )
        )
    }
}