package org.deku.leoz.identity

import org.deku.leoz.SystemInformation
import sx.security.DigestType
import sx.security.instance
import java.security.SecureRandom
import java.util.*
import java.nio.ByteBuffer

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
            val m = DigestType.SHA1.instance()

            val hashBase = arrayOf(
                    systemInformation.hostname,
                    systemInformation.hardwareAddress,
                    systemInformation.networkAddresses.joinToString(", ")).joinToString(";")

            m.update(hashBase.toByteArray())

            // Add salt
            val salt = ByteArray(16)
            sr.nextBytes(salt)
            m.update(salt)

            // Calculate digest and convert ot UUID
            val bb = ByteBuffer.wrap(m.digest())
            val uuid = UUID(bb.getLong(), bb.getLong())

            return Identity(uuid.toString(), name)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}


