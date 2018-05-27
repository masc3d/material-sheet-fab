package org.deku.leoz.mobile.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationSettingsRequest
import android.support.v4.content.ContextCompat
import org.slf4j.LoggerFactory


@SuppressLint("MissingPermission")
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

    val googleApiClientBuilder: GoogleApiClient.Builder by lazy {
        GoogleApiClient.Builder(this)
                .addApi(com.google.android.gms.location.LocationServices.API)
    }

    val googleApiClient: GoogleApiClient by lazy {
        this.googleApiClientBuilder
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val ret = super.onStartCommand(intent, flags, startId)

        if (!googleApiClient.isConnected && !googleApiClient.isConnecting) {
            googleApiClient.connect()
        }

        return ret
    }

    override fun stopService(name: Intent?): Boolean {

        if (googleApiClient.isConnected || googleApiClient.isConnecting) {
            googleApiClient.disconnect()
        }

        return super.stopService(name)
    }

    override fun onDestroy() {

        if (googleApiClient.isConnected || googleApiClient.isConnecting) {
            googleApiClient.disconnect()
        }

        super.onDestroy()
    }

    override fun onConnected(p0: Bundle?) {
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            com.google.android.gms.location.LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener)
    }

    override fun onConnectionSuspended(p0: Int) {
        log.warn("Google API connection suspended!")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        log.warn("Connection to Google API failed. Fallback to android location service.")
        ContextCompat.startForegroundService(this.applicationContext, Intent(applicationContext, LocationServiceAOSP::class.java))
        this.stopSelf()
    }

    val locationListener = LocationListener {
        reportLocation(it)
    }
}