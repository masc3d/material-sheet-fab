package org.deku.leoz.identity

import org.deku.leoz.SystemInformation
import sx.security.DigestType
import sx.security.getInstance
import sx.text.toHexString
import java.security.SecureRandom

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
            val m = DigestType.SHA1.getInstance()

            val hashBase = arrayOf(
                    systemInformation.hostname,
                    systemInformation.hardwareAddress,
                    systemInformation.networkAddresses.joinToString(", ")).joinToString(";")

            m.update(hashBase.toByteArray())

            // Add salt
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


