package sx.android

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context

/**
 * Created by n3 on 03.05.17.
 */
class Connectivity(
        private val context: Context) {

    /**
     * Android's connectivity manager
     */
    private val connectivityManager by lazy { this.context.getConnectivityManager() }

    /**
     * Indicates if there's connectivity
     */
    val isEstablished: Boolean
        get() {
            val connectivityManager = this.context.getConnectivityManager()

            val activeNetworkInfo = connectivityManager.activeNetworkInfo

            if (activeNetworkInfo?.isConnectedOrConnecting ?: false) {
                return true
            }

            /**
             * If active network is not connected nor a connection is pending, check other networks,
             * cause they could be taken from the ConnectionManager for failover. So they might are connecting right now.
             *
             * <getAllNetworks> is only available from API level 21. Project targets level 16
             * See https://developer.android.com/reference/android/net/ConnectivityManager.html#pubmethods for more details
             */
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                if (connectivityManager.allNetworks.firstOrNull { connectivityManager.getNetworkInfo(it).isConnectedOrConnecting } != null) {
                    return true
                }
            } else {
                @Suppress("DEPRECATION")
                if (connectivityManager.allNetworkInfo.firstOrNull { it.isConnectedOrConnecting } != null) {
                    return true
                }
            }


            return false
        }
}