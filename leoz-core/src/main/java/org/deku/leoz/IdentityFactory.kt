package org.deku.leoz

import org.deku.leoz.bundle.BundleType
import sx.security.Algorithms
import sx.text.toHexString
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Identity factory interface
 * Created by masc
 */
abstract class IdentityFactory(
        val name: String
) {
    abstract fun create(): Identity
}

/**
 * Identity factory for desktop devices
 * Created by masc
 */
class DesktopIdentityFactory(
        name: String,
        val systemInformation: SystemInformation)
    : IdentityFactory(name) {

    override fun create(): Identity {
        try {
            // Generate key
            val sr = SecureRandom()
            val m = MessageDigest.getInstance(Algorithms.SHA1)

            val hashBase = arrayOf(
                    systemInformation.hostname,
                    systemInformation.hardwareAddress,
                    systemInformation.networkAddresses.joinToString(", ")).joinToString(";")

            m.update(hashBase.toByteArray())
            val salt = ByteArray(16)
            sr.nextBytes(salt)
            m.update(salt)

            // Calculate digest and format to hex
            val key = m.digest().toHexString()

            return Identity(key, name)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

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


