package org.deku.leoz.mobile.service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import org.threeten.bp.Duration
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import android.location.Location
import android.support.v4.content.ContextCompat
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import org.deku.leoz.mobile.ui.activity.MainActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ApiException
import com.trello.rxlifecycle2.kotlin.bindUntilEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import org.slf4j.LoggerFactory


/**
 * Created by 27694066 on 22.09.2017.
 */
class LocationServiceGMS:
        BaseLocationService(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val locationRequest = locationServices.locationRequest

    var builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

    val googleApiClient: GoogleApiClient by lazy {
        this.locationServices.googleApiClientBuilder
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        googleApiClient.connect()
    }

    override fun onDestroy() {
        googleApiClient.disconnect()
        super.onDestroy()
    }

    override fun onConnected(p0: Bundle?) {
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener)
    }

    override fun onConnectionSuspended(p0: Int) {
        log.warn("Google API connection suspended!")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        log.warn("Connection to Google API failed. Fallback to android location service.")
        ContextCompat.startForegroundService(this.applicationContext, Intent(applicationContext, LocationService::class.java))
        this.stopSelf()
    }

    val locationListener = LocationListener {
        reportLocation(it)
    }
}