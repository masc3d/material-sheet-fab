package org.deku.leoz.mobile.model.service

import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.service.internal.AuthorizationService
import org.deku.leoz.service.internal.NodeServiceV1
import sx.android.Device

fun NodeServiceV1.MobileStatus.Companion.create(identity: Identity, device: Device): NodeServiceV1.MobileStatus {
    return NodeServiceV1.MobileStatus(
            uid = identity.uid.value,
            applicationVersion = BuildConfig.VERSION_NAME,
            serialNumber = device.serial
    )
}