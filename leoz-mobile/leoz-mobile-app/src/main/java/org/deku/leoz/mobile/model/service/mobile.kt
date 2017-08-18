package org.deku.leoz.mobile.model.service

import org.deku.leoz.service.internal.AuthorizationService
import sx.android.Device

/**
 * Mobile device info factory method
 * Created by masc on 18.08.17.
 */
fun AuthorizationService.Mobile.Companion.create(device: Device): AuthorizationService.Mobile {
    /**
     * This IMEI is provided in case the device has none (eg newer emulators)
     */
    val DUMMY_IMEI = "000000000000000"

    return AuthorizationService.Mobile(
            model = device.model.name,
            serial = device.serial,
            imei = if (device.imei.isNotBlank()) device.imei else DUMMY_IMEI
    )
}