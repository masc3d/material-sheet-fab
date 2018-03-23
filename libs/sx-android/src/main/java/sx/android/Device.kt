package sx.android

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.slf4j.LoggerFactory
import sx.text.toHexString
import java.util.*




/**
 * Generic android device class, exposing device specific information like ids and serials
 * Created by masc on 26/02/2017.
 */

@SuppressLint("HardwareIds")
open class Device(private val context: Context) {

    val log = LoggerFactory.getLogger(this.javaClass)

    companion object {
        val SHAREDPREFS_TAG = "sx.androi.device"
        val SHAREDPREFS_KEY_EMUSERIAL = "emu.serial"
    }

    /**
     * Device manfucaturer
     */
    class Manufacturer(val name: String = Build.MANUFACTURER) {
        enum class Type {
            Generic,
            Honeywell,
            Motorola
        }

        /**
         * Manufacturer type
         */
        val type: Type by lazy {
            when {
                this.name.contains("honeywell", ignoreCase = true)  -> Type.Honeywell
                this.name.contains("motorola", ignoreCase = true)   -> Type.Motorola
                else -> Type.Generic
            }
        }

        override fun toString(): String = "Manufacturer(name=${name}, type=${type})"
    }

    /**
     * Indicates if this device is an emulator
     */
    val isEmulator by lazy {
        when (Build.HARDWARE) {
            "goldfish", "ranchu" -> true
            else -> false
        }
    }

    /**
     * Device model
     */
    data class Model(
            /** Model name */
            val name: String = Build.MODEL
    )

    val imei: String by lazy {
        val telephonyManager = this.context.getTelephonyManager()
        try {
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                telephonyManager.imei ?: ""
            else
                telephonyManager.deviceId ?: ""
        } catch(e: SecurityException) { throw e }
    }

    val phone: String by lazy {
        val telephonyManager = this.context.getTelephonyManager()
        try { telephonyManager.line1Number ?: "" } catch(e: SecurityException) { throw e }
    }

    val androidId: String by lazy {
        Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /** Aggregated android version string */
    val androidVersion: String by lazy {
        "${Build.VERSION.CODENAME} ${Build.VERSION.RELEASE} ${Build.VERSION.INCREMENTAL} SDK [${Build.VERSION.SDK_INT}]"
    }

    val vmHeapSize: Long by lazy {
        Runtime.getRuntime().maxMemory()
    }

    /**
     * Device hardware serial number.
     * Returns an artificially generated random serial for emulators which is preserved until the applicaiton is reinstalled.
     */
    val serial: String by lazy {
        when (this.isEmulator) {
            false -> {
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    try { Build.getSerial() } catch (s: SecurityException) { throw s }
                else
                    Build.SERIAL
            }
            // Generate a random serial number for emulators
            true -> {
                // For emulators, generate an artifical random serial and preserve it until the application is reinstalled
                val sharedPrefs = this.context.getSharedPreferences(SHAREDPREFS_TAG, Context.MODE_PRIVATE)
                sharedPrefs.getString(SHAREDPREFS_KEY_EMUSERIAL, null)
                        .let {
                            when (it) {
                                null -> {
                                    val deviceId = "EMU-${UUID.randomUUID().mostSignificantBits.toHexString()}"
                                    sharedPrefs.edit().also {
                                        it.putString(SHAREDPREFS_KEY_EMUSERIAL, deviceId)
                                        it.apply()
                                    }
                                    deviceId
                                }
                                else -> it
                            }
                        }
            }
        }
    }

    val manufacturer: Manufacturer = Manufacturer()
    val model: Model = Model()

    val mobileDateEnabled: Boolean
        get() {
            if (this.manufacturer.type != Manufacturer.Type.Honeywell) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val telephonyManager = this.context.getTelephonyManager()
                    return telephonyManager.isDataEnabled
                } else {
                    // TODO find a reliable way for android versions below API level 26
                    return true
                }
            } else {
                // TODO: inofficial api/configuration setting., likely to break in future versions. should be improved or removed.
                return Settings.Global.getInt(context.contentResolver, "mobile_data", 1) == 1
            }
        }

    val telephonyEnabled: Boolean by lazy {
        try {
            val telephonyManager = this.context.getTelephonyManager()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                telephonyManager.isVoiceCapable
            } else {
                when {
                    telephonyManager.line1Number == null -> false
                    telephonyManager.line1Number.isEmpty() -> telephonyManager.subscriberId != null && telephonyManager.subscriberId.isNotEmpty()
                    else -> true
                }
            }
        } catch (e: SecurityException) {
            false
        }
    }

    val isM2MConnected: Boolean by lazy {
        val telephonyManager = this.context.getTelephonyManager()
        try {
            log.debug("NetworkOperator [${telephonyManager.networkOperator}]")
            val mcc = telephonyManager.networkOperator.substring(0, 3)
            val mnc = telephonyManager.networkOperator.substring(3)
            log.debug("MCC [$mcc] MNC [$mnc]")
            (mcc == "901" && mnc == "28")
        } catch (e: Exception) {
            log.warn("NetworkOperator information could not be determined", e)
            false
        }
    }

    val googleApiSupported: Boolean by lazy {
        var ret: Boolean = true
        val apiAvailability = GoogleApiAvailability.getInstance()
        val status = apiAvailability.isGooglePlayServicesAvailable(this.context)

        if(status != ConnectionResult.SUCCESS) {
            log.warn("GooglePlay-Services are not supported by this device")
            ret = false
        }

        ret
    }

    override fun toString(): String =
            "Device(imei=${imei}, androidId=${androidId}) androidVersion=${androidVersion} serial=${serial} manufacturer=${manufacturer} model=${model} vmHeapSize=${vmHeapSize}"
}