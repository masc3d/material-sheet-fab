package org.deku.leoz

import sx.security.DigestType
import sx.security.getInstance
import sx.text.toHexString

fun hashUserPassword(salt: ByteArray,
                     email: String,
                     password: String): String {

    val m = DigestType.SHA1.getInstance()
    m.update(salt)
    m.update(email.toByteArray())
    m.update(password.toByteArray())
    return m.digest().toHexString()
}