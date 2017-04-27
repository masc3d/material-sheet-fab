package org.deku.leoz.mobile

import org.junit.Test
import sx.security.Algorithms
import sx.text.toHexString
import java.security.MessageDigest

/**
 * Created by n3 on 27.04.17.
 */
class HashTest {
    val PASSWORD = "testtest"

    @Test
    fun testSha256() {
        val md = MessageDigest.getInstance(Algorithms.SHA256)
        println(md
                .digest(PASSWORD.toByteArray())
                .toHexString())
    }
}