package org.deku.leoz.identity

import org.deku.leoz.bundle.BundleType
import sx.security.DigestType
import sx.security.instance
import java.nio.ByteBuffer
import java.util.*

/**
 * Identity factory for mobile devices
 * Created by masc
 */
class MobileIdentityFactory(
        val serial: String,
        val imei: String)
    : IdentityFactory(name = BundleType.LEOZ_MOBILE.value) {

    override fun create(): Identity {
        val m = DigestType.SHA1.instance()

        val hashBase = arrayOf(
                serial,
                imei).joinToString(",")

        m.update(hashBase.toByteArray())

        // Calculate digest and convert ot UUID
        val bb = ByteBuffer.wrap(m.digest())
        val uuid = UUID(bb.getLong(), bb.getLong())

        return Identity(uid = uuid.toString(), name = this.name)
    }
}