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

/**
 * Created by 27694066 on 22.09.2017.
 */
class LocationServiceGMS(
        var period: Duration = Duration.ofSeconds(30),
        var minDistance: Float = 250F,
        var enabled: Boolean = true
):
        BaseLocationService(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    val googleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(applicationContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        googleApiClient.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        googleApiClient.disconnect()
    }

    override fun onConnected(p0: Bundle?) {
        val locationRequest = LocationRequest().also {
            it.interval = this.period.toMillis()
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            it.smallestDisplacement = 500F
        }
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener)
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val locationListener = LocationListener {
        reportLocation(it)
    }
}