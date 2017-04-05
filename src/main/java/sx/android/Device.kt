package sx.android

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager

/**
 * Generic android device class, exposing device specific information like ids and serials
 * Created by masc on 26/02/2017.
 */
open class Device(private val context: Context) {
    /**
     * Device manfucaturer
     */
    class Manufacturer(val name: String = Build.MANUFACTURER) {
        enum class Type {
            Generic,
            Honeywell,
            Motorola,
        }
        /**
         * Manufacturer type
         */
        val type: Type by lazy {
            if (this.name.contains("honeywell",ignoreCase = true))
                Type.Honeywell
            else if (this.name.contains("motorola", ignoreCase = true))
                Type.Motorola
            else
                Type.Generic
        }

        override fun toString(): String {
            return "Manufacturer(name=${name}, type=${type})"
        }
    }

    /**
     * Device model
     */
    data class Model(val name: String = Build.MODEL) { }

    val imei: String by lazy {
        val telephonyManager = this.context.getTelephonyManager()
        telephonyManager.deviceId
    }

    val androidId: String by lazy {
        Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    val serial: String  = Build.SERIAL
    val manufacturer: Manufacturer = Manufacturer()
    val model: Model = Model()

    override fun toString(): String {
        return "Device(imei=${imei}, androidId=${androidId}) serial=${serial} manufacturer=${manufacturer} model=${model}"
    }
}