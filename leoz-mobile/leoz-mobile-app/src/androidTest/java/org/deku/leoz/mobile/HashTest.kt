package org.deku.leoz.mobile

import org.junit.Test
import sx.security.DigestType
import sx.text.toHexString
import java.security.MessageDigest

/**
* Created by masc on 27.04.17.
*/
class HashTest {
    val PASSWORD = "testtest"

    @Test
    fun testSha256() {
        val md = MessageDigest.getInstance(DigestType.SHA256.value)
        println(md
                .digest(PASSWORD.toByteArray())
                .toHexString())
    }
}