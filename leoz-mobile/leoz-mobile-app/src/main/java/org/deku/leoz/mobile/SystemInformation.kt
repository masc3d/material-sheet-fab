package org.deku.leoz.mobile

import io.swagger.annotations.ApiModelProperty
import sx.android.Device
import sx.io.serialization.Serializable

/**
 * Mobile system information, sent with authorization message
 * Created by masc on 08.09.17.
 */
@Serializable
data class SystemInformation(
        /** Device model */
        var model: String = "",
        /** Device serial number */
        var serial: String = "",
        /** Device IMEI */
        var imei: String = "",
        /** Android version */
        var androidVersion: String = "",
        /** Current application version */
        val applicationVersion: String
) {
    companion object {}
}

/**
 * Extension for creating system information structure
 */
fun SystemInformation.Companion.create(
        application: Application,
        device: Device): SystemInformation {
    /**
     * This IMEI is provided in case the device has none (eg newer emulators)
     */
    val DUMMY_IMEI = "000000000000000"

    return SystemInformation(
            model = device.model.name,
            serial = device.serial,
            imei = if (device.imei.isNotBlank()) device.imei else DUMMY_IMEI,
            androidVersion = device.androidVersion,
            applicationVersion = application.version
    )
}