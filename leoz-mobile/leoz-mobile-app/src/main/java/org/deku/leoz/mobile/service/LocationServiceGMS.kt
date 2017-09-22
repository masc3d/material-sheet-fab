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
import org.deku.leoz.mobile.ui.activity.MainActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ApiException





/**
 * Created by 27694066 on 22.09.2017.
 */
class LocationServiceGMS(
        var period: Duration = Duration.ofSeconds(30),
        var enabled: Boolean = true
):
        BaseLocationService(),
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    val locationRequest = LocationRequest().also {
        it.interval = this.period.toMillis()
        it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        it.smallestDisplacement = 200F
    }

    var builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

    val googleApiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(applicationContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    val settingsClient = LocationServices.getSettingsClient(this)
    val task = settingsClient.checkLocationSettings(builder.build()).also {
        it.addOnSuccessListener {

        }

        it.addOnFailureListener {
            val statusCode = (it as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val resolvable = it as ResolvableApiException
                        resolvable.startResolutionForResult(this@Activity,
                                REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// Location settings are not satisfied. However, we have no way
            // to fix the settings so we won't show the dialog.
        }
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