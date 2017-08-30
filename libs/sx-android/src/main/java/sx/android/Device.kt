package sx.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import java.util.*

/**
 * Generic android device class, exposing device specific information like ids and serials
 * Created by masc on 26/02/2017.
 */
open class Device(private val context: Context) {

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
            Motorola,
        }

        /**
         * Manufacturer type
         */
        val type: Type by lazy {
            if (this.name.contains("honeywell", ignoreCase = true))
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
    data class Model(val name: String = Build.MODEL) {}

    val imei: String by lazy {
        val telephonyManager = this.context.getTelephonyManager()
        telephonyManager.deviceId ?: ""
    }

    val phone: String by lazy {
        val telephonyManager = this.context.getTelephonyManager()
        telephonyManager.line1Number ?: ""
    }

    val androidId: String by lazy {
        Settings.Secure.getString(this.context.getContentResolver(), Settings.Secure.ANDROID_ID);
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
            false -> Build.SERIAL
            // Generate a random serial number for emulators
            true -> {
                // For emulators, generate an artifical random serial and preserve it until the application is reinstalled
                val sharedPrefs = this.context.getSharedPreferences(SHAREDPREFS_TAG, Context.MODE_PRIVATE)
                sharedPrefs.getString(SHAREDPREFS_KEY_EMUSERIAL, null)
                        .let {
                            when (it) {
                                null -> {
                                    val deviceId = "EMU-${UUID.randomUUID().mostSignificantBits.toString(16)}"
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

    override fun toString(): String {
        return "Device(imei=${imei}, androidId=${androidId}) serial=${serial} manufacturer=${manufacturer} model=${model} vmHeapSize=${vmHeapSize}"
    }
}