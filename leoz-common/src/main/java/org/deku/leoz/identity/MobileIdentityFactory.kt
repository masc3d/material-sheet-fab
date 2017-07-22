package org.deku.leoz.identity

import org.deku.leoz.bundle.BundleType
import sx.security.DigestType
import sx.security.getInstance
import sx.text.toHexString

/**
 * Identity factory for mobile devices
 * Created by masc
 */
class MobileIdentityFactory(
        val serial: String,
        val imei: String)
    : IdentityFactory(name = BundleType.LeozMobile.value) {

    override fun create(): Identity {
        val m = DigestType.SHA1.getInstance()

        val hashBase = arrayOf(
                serial,
                imei).joinToString(",")

        m.update(hashBase.toByteArray())

        // Calculate digest and format to hex
        val key = m.digest().toHexString()
        return Identity(uid = key, name = this.name)
    }
}