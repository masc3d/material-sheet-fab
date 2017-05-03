package sx.android

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
            val activeNetworkInfo = connectivityManager.activeNetworkInfo

            if (activeNetworkInfo?.isConnectedOrConnecting ?: false) {
                return true
            }

            // TODO: comment why it's not sufficient to check active network & deprecated property is used
            if (connectivityManager.allNetworkInfo.firstOrNull { it.isConnectedOrConnecting } != null)
                return true

            return false
        }
}