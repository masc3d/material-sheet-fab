package org.deku.leoz.mobile.model.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.deku.leoz.bundle.BundleType
import org.deku.leoz.identity.Identity
import org.deku.leoz.mobile.Application
import org.deku.leoz.mobile.BuildConfig
import org.deku.leoz.mobile.SystemInformation
import org.deku.leoz.mobile.create
import org.deku.leoz.service.internal.NodeServiceV1
import sx.android.Device

fun NodeServiceV1.Info.Companion.create(application: Application, identity: Identity, device: Device): NodeServiceV1.Info {
    return NodeServiceV1.Info(
            uid = identity.uid.value,
            bundleName = BundleType.LEOZ_MOBILE.value,
            bundleVersion = BuildConfig.VERSION_NAME,
            hardwareSerialNumber = device.serial,
            systemInformation = SystemInformation.create(
                    application = application,
                    device = device).let {
                ObjectMapper().writeValueAsString(it)
            }
    )
}