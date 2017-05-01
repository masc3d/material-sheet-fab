package org.deku.leoz.mobile

import org.deku.leoz.Identity
import org.deku.leoz.IdentityFactory
import org.deku.leoz.bundle.BundleType

/**
 * Identity factory for mobile devices
 * Created by masc
 */
class MobileIdentityFactory(
        val imei: String)
    : IdentityFactory(name = BundleType.LEOZ_MOBILE.value) {

    override fun create(): Identity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}