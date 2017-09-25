package org.deku.leoz.mobile

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import org.threeten.bp.Duration

/**
 * Created by 27694066 on 25.09.2017.
 */
class LocationServices(context: Context) {

    val locationSettings: LocationSettings by Kodein.global.lazy.instance()

    val locationRequest = LocationRequest().also {
        it.interval = Duration.ofSeconds(locationSettings.period).toMillis()
        it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        it.smallestDisplacement = 200F
    }

    var builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

//    val settingsClient = LocationServices.getSettingsClient(this)
//    val task = settingsClient.checkLocationSettings(builder.build()).also {
//        it.addOnSuccessListener {
//
//        }
//
//        it.addOnFailureListener {
//            //            val statusCode = (it as ApiException).statusCode
////            when (statusCode) {
////                CommonStatusCodes.RESOLUTION_REQUIRED ->
////                    // Location settings are not satisfied, but this can be fixed
////                    // by showing the user a dialog.
////                    try {
////                        // Show the dialog by calling startResolutionForResult(),
////                        // and check the result in onActivityResult().
////                        val resolvable = it as ResolvableApiException
////                        resolvable.startResolutionForResult(this@Activity,
////                                REQUEST_CHECK_SETTINGS)
////                    } catch (sendEx: IntentSender.SendIntentException) {
////                        // Ignore the error.
////                    }
////
////                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
////                }
////            }// Location settings are not satisfied. However, we have no way
////            // to fix the settings so we won't show the dialog.
//        }
//    }
}